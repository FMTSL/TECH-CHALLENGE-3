# Deploy na AWS (ECS Fargate)

Guia completo para publicar a aplicação na AWS usando o Free Tier, sem Load Balancer (o Fargate recebe um IP público direto — suficiente para demonstrar o deploy em nuvem exigido pelo enunciado; um ALB pode ser adicionado depois se necessário).

Tempo estimado: 30–40 minutos. Custo: dentro do Free Tier se a conta AWS tiver menos de 12 meses e os recursos forem removidos ao final (ver seção de limpeza).

## 0. Pré-requisitos

- Conta AWS ativa, com um usuário IAM com permissões administrativas (ou as políticas `AmazonECS_FullAccess`, `AmazonEC2ContainerRegistryFullAccess`, `AmazonRDSFullAccess`, `IAMFullAccess`, `CloudWatchLogsFullAccess`).
- [AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) instalado.
- Docker instalado e funcionando (já usado para rodar o projeto localmente).
- O projeto já validado localmente com `docker compose up` antes de prosseguir — deploy na nuvem não deve ser o primeiro teste da aplicação.

Configura o CLI com suas credenciais (Access Key e Secret Key são geradas em IAM → Users → Security credentials):

```powershell
aws configure
# AWS Access Key ID: ...
# AWS Secret Access Key: ...
# Default region name: us-east-1
# Default output format: json
```

Define a região como variável para reutilizar nos comandos seguintes:

```powershell
$REGION = "us-east-1"
$ACCOUNT_ID = (aws sts get-caller-identity --query Account --output text)
```

## 1. Rede: identificar a VPC padrão e liberar a porta 8080

Toda conta AWS nova já vem com uma VPC padrão — não é preciso criar uma.

```powershell
$VPC_ID = (aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query "Vpcs[0].VpcId" --output text)
$SUBNET_ID = (aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPC_ID" --query "Subnets[0].SubnetId" --output text)

echo "VPC: $VPC_ID"
echo "Subnet: $SUBNET_ID"
```

Cria um Security Group liberando a porta 8080 (API) publicamente e a 5432 (Postgres) apenas para recursos do próprio grupo:

```powershell
$SG_ID = (aws ec2 create-security-group --group-name booking-beleza-sg --description "Booking Beleza" --vpc-id $VPC_ID --query "GroupId" --output text)

aws ec2 authorize-security-group-ingress --group-id $SG_ID --protocol tcp --port 8080 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-ingress --group-id $SG_ID --protocol tcp --port 5432 --source-group $SG_ID
```

## 2. Banco de dados (RDS PostgreSQL)

```powershell
aws rds create-db-instance `
  --db-instance-identifier booking-beleza-db `
  --db-instance-class db.t3.micro `
  --engine postgres `
  --engine-version 16.4 `
  --master-username booking `
  --master-user-password "TrocarPorUmaSenhaForte123!" `
  --allocated-storage 20 `
  --db-name booking_beleza `
  --vpc-security-group-ids $SG_ID `
  --no-publicly-accessible `
  --backup-retention-period 0
```

A criação leva de 5 a 10 minutos. Aguarda ficar disponível e captura o endpoint:

```powershell
aws rds wait db-instance-available --db-instance-identifier booking-beleza-db

$DB_ENDPOINT = (aws rds describe-db-instances --db-instance-identifier booking-beleza-db --query "DBInstances[0].Endpoint.Address" --output text)
echo $DB_ENDPOINT
```

Guarda esse endereço — é o `RDS_ENDPOINT` usado na task definition (passo 5).

`db.t3.micro` com 20GB está dentro do Free Tier de 12 meses. Se a conta já passou desse período, considera reduzir o `allocated-storage` ou usar outra classe.

## 3. Imagem: build e push para o ECR

```powershell
aws ecr create-repository --repository-name booking-beleza

aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"

docker build -t booking-beleza .
docker tag booking-beleza:latest "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/booking-beleza:latest"
docker push "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/booking-beleza:latest"
```

## 4. Papel de execução do ECS (IAM)

O Fargate precisa de uma role para puxar a imagem do ECR e escrever logs. Se a conta nunca usou ECS antes, essa role provavelmente não existe ainda:

```powershell
aws iam get-role --role-name ecsTaskExecutionRole
```

Se retornar erro `NoSuchEntity`, cria a role:

```powershell
$trustPolicy = @"
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": { "Service": "ecs-tasks.amazonaws.com" },
    "Action": "sts:AssumeRole"
  }]
}
"@
$trustPolicy | Out-File -Encoding ascii trust-policy.json

aws iam create-role --role-name ecsTaskExecutionRole --assume-role-policy-document file://trust-policy.json
aws iam attach-role-policy --role-name ecsTaskExecutionRole --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
```

## 5. Task Definition

Edita `infra/aws/task-definition.json` (já incluído no repositório) substituindo:

| Placeholder | Substituir por |
|---|---|
| `ACCOUNT_ID` (duas ocorrências) | valor de `$ACCOUNT_ID` |
| `REGION` (duas ocorrências) | valor de `$REGION` |
| `RDS_ENDPOINT` | valor de `$DB_ENDPOINT` (passo 2) |
| `SENHA_DO_RDS` | a senha definida no `create-db-instance` |
| `SEGREDO_COM_NO_MINIMO_32_CARACTERES` | qualquer string aleatória com 32+ caracteres |

Registra a task definition:

```powershell
aws ecs register-task-definition --cli-input-json file://infra/aws/task-definition.json
```

## 6. Cluster e serviço

```powershell
aws ecs create-cluster --cluster-name booking-beleza-cluster

aws ecs create-service `
  --cluster booking-beleza-cluster `
  --service-name booking-beleza-service `
  --task-definition booking-beleza `
  --desired-count 1 `
  --launch-type FARGATE `
  --network-configuration "awsvpcConfiguration={subnets=[$SUBNET_ID],securityGroups=[$SG_ID],assignPublicIp=ENABLED}"
```

## 7. Encontrar o IP público e testar

O deploy leva 1 a 3 minutos até o container ficar `RUNNING`.

```powershell
$TASK_ARN = (aws ecs list-tasks --cluster booking-beleza-cluster --service-name booking-beleza-service --query "taskArns[0]" --output text)

aws ecs wait tasks-running --cluster booking-beleza-cluster --tasks $TASK_ARN

$ENI_ID = (aws ecs describe-tasks --cluster booking-beleza-cluster --tasks $TASK_ARN --query "tasks[0].attachments[0].details[?name=='networkInterfaceId'].value" --output text)

$PUBLIC_IP = (aws ec2 describe-network-interfaces --network-interface-ids $ENI_ID --query "NetworkInterfaces[0].Association.PublicIp" --output text)

echo "Aplicacao disponivel em: http://$PUBLIC_IP:8080/swagger-ui.html"
```

Abre a URL retornada no navegador. Se não abrir de imediato, aguarda mais 1–2 minutos (a aplicação ainda está subindo — migrations do Flyway, JPA, etc.) e atualiza a página.

Para testar com a coleção do Postman (`postman/booking-beleza.postman_collection.json`), edita a variável `baseUrl` da coleção de `http://localhost:8080` para `http://SEU_IP_PUBLICO:8080`.

## 8. Acompanhar logs

```powershell
aws logs tail /ecs/booking-beleza --follow
```

## 9. Limpeza (importante — evita cobrança)

O IP público muda a cada novo deploy (task recriada), então isso não é adequado para uso permanente — é suficiente para demonstrar o funcionamento e depois desligar. Ao terminar os testes:

```powershell
aws ecs update-service --cluster booking-beleza-cluster --service booking-beleza-service --desired-count 0
aws ecs delete-service --cluster booking-beleza-cluster --service booking-beleza-service --force
aws ecs delete-cluster --cluster booking-beleza-cluster

aws rds delete-db-instance --db-instance-identifier booking-beleza-db --skip-final-snapshot

aws ecr delete-repository --repository-name booking-beleza --force

aws ec2 delete-security-group --group-id $SG_ID
```

A exclusão do RDS também leva alguns minutos; confirma em **RDS → Databases** no console que o status mudou para `deleting` antes de considerar concluído.

## Troubleshooting

| Sintoma | Causa provável |
|---|---|
| Task para (`STOPPED`) logo após iniciar | Ver `aws ecs describe-tasks ... --query "tasks[0].stoppedReason"`. Geralmente falha ao conectar no RDS (endpoint/senha errados na task definition) ou variável `JWT_SECRET` ausente |
| `CannotPullContainerError` | Imagem não foi enviada ao ECR corretamente, ou a URI na task definition não bate com `$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/booking-beleza:latest` |
| Task fica `RUNNING` mas Swagger não abre | Security Group sem a porta 8080 liberada, ou a task ainda está subindo (Flyway/JPA) — aguardar e conferir logs |
| `ecsTaskExecutionRole` não encontrada ao registrar a task | Repetir o passo 4 |
