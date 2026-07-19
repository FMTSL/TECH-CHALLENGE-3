# Deploy na AWS (ECS Fargate)

Pré-requisitos: AWS CLI configurado, um repositório no ECR, um banco RDS Postgres (ou o container `db` em outra instância acessível pela VPC).

## 1. Build e push da imagem para o ECR

```bash
aws ecr create-repository --repository-name booking-beleza

aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

docker build -t booking-beleza .
docker tag booking-beleza:latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/booking-beleza:latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/booking-beleza:latest
```

## 2. Parâmetros sensíveis (SSM Parameter Store)

```bash
aws ssm put-parameter --name /booking-beleza/db-url --type SecureString \
  --value "jdbc:postgresql://<rds-endpoint>:5432/booking_beleza"
aws ssm put-parameter --name /booking-beleza/db-user --type SecureString --value "booking"
aws ssm put-parameter --name /booking-beleza/db-password --type SecureString --value "<senha-forte>"
aws ssm put-parameter --name /booking-beleza/jwt-secret --type SecureString --value "<segredo-com-32-chars>"
```

## 3. Registrar a task definition e criar o serviço

```bash
# edite infra/aws/task-definition.json: substitua <SEU_REGISTRY>, REGION e ACCOUNT_ID
aws ecs register-task-definition --cli-input-json file://infra/aws/task-definition.json

aws ecs create-cluster --cluster-name booking-beleza-cluster

aws ecs create-service \
  --cluster booking-beleza-cluster \
  --service-name booking-beleza-service \
  --task-definition booking-beleza \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[<subnet-id>],securityGroups=[<sg-id>],assignPublicIp=ENABLED}"
```

## 4. Exponha via Application Load Balancer

Crie um ALB apontando para o target group da porta 8080, com health check em `/actuator/health`. O `docker-compose.yml` já inclui esse endpoint liberado no `SecurityConfig`.

## 5. CI/CD (opcional)

O workflow em `.github/workflows/ci.yml` já builda a imagem a cada push. Para publicar automaticamente no ECR, adicione um job extra com `aws-actions/amazon-ecr-login` e `aws ecs update-service --force-new-deployment` após o merge em `main`.
