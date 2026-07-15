#!/bin/bash
set -e

echo "构建所有微服务..."

mvn clean package -DskipTests

docker build -t myweb2025.azurecr.io/api-gateway:latest ./api-gateway
docker build -t myweb2025.azurecr.io/user-service:latest ./user-service
docker build -t myweb2025.azurecr.io/content-service:latest ./content-service
docker build -t myweb2025.azurecr.io/transaction-service:latest ./transaction-service
docker build -t myweb2025.azurecr.io/admin-service:latest ./admin-service
docker build -t myweb2025.azurecr.io/notification-service:latest ./notification-service

docker push myweb2025.azurecr.io/api-gateway:latest
docker push myweb2025.azurecr.io/user-service:latest
docker push myweb2025.azurecr.io/content-service:latest
docker push myweb2025.azurecr.io/transaction-service:latest
docker push myweb2025.azurecr.io/admin-service:latest
docker push myweb2025.azurecr.io/notification-service:latest

echo "所有服务构建完成！"

