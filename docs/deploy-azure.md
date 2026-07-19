# Deploy no Azure (App Service for Containers)

Pré-requisitos: Azure CLI (`az login`), um Azure Container Registry (ACR) e um Azure Database for PostgreSQL.

## 1. Build e push da imagem para o ACR

```bash
az group create --name booking-beleza-rg --location brazilsouth

az acr create --resource-group booking-beleza-rg --name bookingbelezaacr --sku Basic
az acr login --name bookingbelezaacr

docker build -t bookingbelezaacr.azurecr.io/booking-beleza:latest .
docker push bookingbelezaacr.azurecr.io/booking-beleza:latest
```

## 2. Banco de dados gerenciado

```bash
az postgres flexible-server create \
  --resource-group booking-beleza-rg \
  --name booking-beleza-db \
  --admin-user booking \
  --admin-password "<senha-forte>" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --database-name booking_beleza
```

## 3. App Service apontando para a imagem

```bash
az appservice plan create --name booking-beleza-plan --resource-group booking-beleza-rg \
  --is-linux --sku B1

az webapp create --resource-group booking-beleza-rg --plan booking-beleza-plan \
  --name booking-beleza-app --deployment-container-image-name bookingbelezaacr.azurecr.io/booking-beleza:latest

az webapp config appsettings set --resource-group booking-beleza-rg --name booking-beleza-app --settings \
  SPRING_PROFILES_ACTIVE=docker \
  SPRING_DATASOURCE_URL="jdbc:postgresql://booking-beleza-db.postgres.database.azure.com:5432/booking_beleza" \
  SPRING_DATASOURCE_USERNAME="booking" \
  SPRING_DATASOURCE_PASSWORD="<senha-forte>" \
  JWT_SECRET="<segredo-com-32-chars>" \
  WEBSITES_PORT=8080
```

## 4. Health check e logs

```bash
az webapp config set --resource-group booking-beleza-rg --name booking-beleza-app --health-check-path /actuator/health
az webapp log tail --resource-group booking-beleza-rg --name booking-beleza-app
```

A aplicação fica disponível em `https://booking-beleza-app.azurewebsites.net`.
