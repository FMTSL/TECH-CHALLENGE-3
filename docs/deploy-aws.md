# Deploy na AWS (ECS Fargate) com banco no Render

> **Este é o ambiente de produção atual do projeto** — guia validado, não apenas teórico. A aplicação está no ar em um cluster ECS Fargate, conectada ao Postgres gerenciado do Render.

Guia para publicar a aplicação na AWS usando o Free Tier, sem Load Balancer (o Fargate recebe um IP público direto — suficiente para demonstrar o deploy em nuvem exigido pelo enunciado). O banco de dados fica hospedado no Render (Postgres gerenciado, plano free) e é acessado pela aplicação na AWS através da internet.

Tempo estimado: 20–30 minutos. Custo: dentro do Free Tier se a conta AWS tiver menos de 12 meses e os recursos forem removidos ao final (ver seção de limpeza).

## 0. Pré-requisitos

- Conta AWS ativa, com um usuário IAM com permissões administrativas (ou as políticas `AmazonECS_FullAccess`, `AmazonEC2ContainerRegistryFullAccess`, `IAMFullAccess`, `CloudWatchLogsFullAccess`).
- [AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) instalado.
- Docker instalado e funcionando (já usado para rodar o projeto localmente).
- Um banco Postgres já criado no Render (plano free), com a **External Database URL** disponível no painel do serviço.
- O projeto já validado localmente com `docker compose up` antes de prosseguir.

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

Cria um Security Group liberando a porta 8080 (API) publicamente. Não é necessário abrir a porta 5432: o banco não está nesta VPC, e o tráfego de saída (para o Postgres do Render, pela internet) já é liberado por padrão em qualquer Security Group novo, que permite todo o egress a menos que seja restringido explicitamente.

```powershell
$SG_ID = (aws ec2 create-security-group --group-name booking-beleza-sg --description "Booking Beleza" --vpc-id $VPC_ID --query "GroupId" --output text)

aws ec2 authorize-security-group-ingress --group-id $SG_ID --protocol tcp --port 8080 --cidr 0.0.0.0/0
```

## 2. Banco de dados: Postgres externo (Render)

Não é preciso criar nenhum banco na AWS — a aplicação vai se conectar ao Postgres que já existe no Render, pela **External Database URL** (não a Internal, que só funciona dentro da rede privada do próprio Render).

No painel do serviço de banco no Render, anota:

- **Hostname externo**: algo como `dpg-xxxxxxxxxxxxx-a.oregon-postgres.render.com` (não o hostname interno, mais curto, que não resolve fora do Render)
- **Database**, **Username**, **Password**: os mesmos exibidos no painel

O Render exige conexão criptografada para acessos externos, então a URL JDBC precisa incluir `?sslmode=require`:

```
jdbc:postgresql://<hostname-externo-do-render>:5432/booking_beleza?sslmode=require
```

Esses três valores (host, usuário, senha) entram na task definition no próximo passo. Como esse arquivo fica versionado no repositório, ele contém apenas placeholders (`RENDER_DB_HOST`, `RENDER_DB_PASSWORD`) — os valores reais são substituídos localmente, sem serem commitados.

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
| `RENDER_DB_HOST` | o hostname externo do banco no Render (passo 2) |
| `RENDER_DB_PASSWORD` | a senha do banco no Render |
| `SEGREDO_COM_NO_MINIMO_32_CARACTERES` | qualquer string aleatória com 32+ caracteres |

**Importante:** faz essa edição só localmente, sem commitar o arquivo com os valores reais preenchidos — a versão no repositório deve continuar com os placeholders. Se quiser manter os valores reais versionados de forma segura, a evolução natural é mover `RENDER_DB_PASSWORD`/`JWT_SECRET` para o AWS Systems Manager Parameter Store e referenciá-los via `secrets` na task definition, em vez de `environment` (ver comentário no arquivo de exemplo anterior deste guia, se precisar dessa variante).

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

aws ecr delete-repository --repository-name booking-beleza --force

aws ec2 delete-security-group --group-id $SG_ID
```

O banco no Render continua existindo independente disso — se quiser removê-lo também, isso é feito pelo painel do Render, não pela AWS CLI.

## Troubleshooting

| Sintoma | Causa provável |
|---|---|
| Task para (`STOPPED`) logo após iniciar | Ver `aws ecs describe-tasks ... --query "tasks[0].stoppedReason"`. Geralmente falha ao conectar no Postgres do Render (host/senha errados na task definition, ou usou o hostname interno em vez do externo) ou variável `JWT_SECRET` ausente |
| Erro relacionado a SSL na conexão com o banco | Confirma que a URL JDBC tem `?sslmode=require` no final — o Render exige conexão criptografada para acessos externos |
| `CannotPullContainerError` | Imagem não foi enviada ao ECR corretamente, ou a URI na task definition não bate com `$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/booking-beleza:latest` |
| Task fica `RUNNING` mas Swagger não abre | Security Group sem a porta 8080 liberada, ou a task ainda está subindo (Flyway/JPA) — aguardar e conferir logs |
| `ecsTaskExecutionRole` não encontrada ao registrar a task | Repetir o passo 4 |
| Log mostra `Connection refused` tentando enviar e-mail | Esperado e inofensivo — não há servidor SMTP configurado neste deploy; a falha é capturada e logada como aviso, sem afetar o funcionamento da API |
