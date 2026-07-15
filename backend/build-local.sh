#!/bin/bash
# 本地构建Docker镜像脚本（不推送到云端）

set -e

echo "=========================================="
echo "  本地构建所有微服务Docker镜像"
echo "=========================================="
echo ""

# 编译项目
echo "[1/2] 编译Maven项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败！"
    exit 1
fi

echo "✅ 编译成功"
echo ""

# 构建Docker镜像（使用本地标签）
echo "[2/2] 构建Docker镜像..."
echo ""

docker build -t ournovel/api-gateway:local ./api-gateway
echo "✅ api-gateway 镜像构建完成"

docker build -t ournovel/user-service:local ./user-service
echo "✅ user-service 镜像构建完成"

docker build -t ournovel/content-service:local ./content-service
echo "✅ content-service 镜像构建完成"

docker build -t ournovel/transaction-service:local ./transaction-service
echo "✅ transaction-service 镜像构建完成"

docker build -t ournovel/admin-service:local ./admin-service
echo "✅ admin-service 镜像构建完成"

docker build -t ournovel/notification-service:local ./notification-service
echo "✅ notification-service 镜像构建完成"

echo ""
echo "=========================================="
echo "  所有镜像构建完成！"
echo "=========================================="
echo ""
echo "镜像列表："
docker images | grep ournovel
echo ""
echo "启动服务：docker-compose up -d"
echo ""

