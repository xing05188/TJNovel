#!/bin/bash
set -e

echo "部署微服务到ACI..."

RESOURCE_GROUP=${RESOURCE_GROUP:-MyWeb}
ACR_NAME=${ACR_NAME:-myweb2025}
REGISTRY="${ACR_NAME}.azurecr.io"

ACR_PASSWORD=$(az acr credential show --name "${ACR_NAME}" --query "passwords[0].value" -o tsv)

deploy_container() {
  local name=$1
  local image=$2
  local port=$3
  shift 3
  az container create \
    --resource-group "${RESOURCE_GROUP}" \
    --name "${name}" \
    --image "${REGISTRY}/${image}:latest" \
    --cpu 1 --memory 1 --ports "${port}" \
    --ip-address Public \
    --registry-login-server "${REGISTRY}" \
    --registry-username "${ACR_NAME}" \
    --registry-password "${ACR_PASSWORD}" \
    "$@"
}

deploy_container user-service user-service 7081
deploy_container content-service content-service 7082
deploy_container transaction-service transaction-service 7083
deploy_container admin-service admin-service 7084

USER_IP=$(az container show --resource-group "${RESOURCE_GROUP}" --name user-service --query "ipAddress.ip" -o tsv)
CONTENT_IP=$(az container show --resource-group "${RESOURCE_GROUP}" --name content-service --query "ipAddress.ip" -o tsv)
TX_IP=$(az container show --resource-group "${RESOURCE_GROUP}" --name transaction-service --query "ipAddress.ip" -o tsv)
ADMIN_IP=$(az container show --resource-group "${RESOURCE_GROUP}" --name admin-service --query "ipAddress.ip" -o tsv)

deploy_container notification-service notification-service 7085 \
  --environment-variables AZURE_SERVICEBUS_CONNECTION_STRING="${AZURE_SERVICEBUS_CONNECTION_STRING:-}" AZURE_SERVICEBUS_QUEUE="${AZURE_SERVICEBUS_QUEUE:-notifications}"

deploy_container api-gateway api-gateway 7080 \
  --environment-variables \
    SERVICES_USER="http://${USER_IP}:7081" \
    SERVICES_CONTENT="http://${CONTENT_IP}:7082" \
    SERVICES_TRANSACTION="http://${TX_IP}:7083" \
    SERVICES_ADMIN="http://${ADMIN_IP}:7084" \
    SERVICES_NOTIFICATION="http://$(az container show --resource-group "${RESOURCE_GROUP}" --name notification-service --query "ipAddress.ip" -o tsv):7085"

echo "部署完成！"

