# Docker 容器化部署指南

## 📦 容器化架构

每个微服务都运行在独立的 Docker 容器中：
- ✅ **隔离性**：每个服务独立运行，互不影响
- ✅ **可移植性**：一次构建，到处运行
- ✅ **可扩展性**：可以轻松扩展单个服务
- ✅ **一致性**：开发、测试、生产环境一致

## 🚀 快速开始

### 1. 使用 Docker Compose（本地和生产环境通用）

#### 配置环境变量（可选）

```bash
# 复制环境变量示例文件
cp .env.example .env

# 编辑 .env 文件，设置你的数据库连接信息
# 如果不创建 .env 文件，将使用 docker-compose.yml 中的默认值
```

#### 启动所有服务

```bash
# 启动所有服务（使用 Azure MySQL）
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

**注意**：配置使用 Azure MySQL 数据库。确保：
- Azure MySQL 服务器可访问
- 防火墙规则允许你的 IP 地址访问
- 数据库用户名和密码正确

### 2. 构建 Docker 镜像

#### 方式1：使用构建脚本（推荐）

```bash
# Linux/Mac
./build-all.sh

# Windows PowerShell
bash build-all.sh
```

#### 方式2：手动构建单个服务

```bash
# 先编译项目
mvn clean package -DskipTests

# 构建镜像
docker build -t user-service:latest ./user-service
docker build -t content-service:latest ./content-service
docker build -t transaction-service:latest ./transaction-service
docker build -t admin-service:latest ./admin-service
docker build -t notification-service:latest ./notification-service
docker build -t api-gateway:latest ./api-gateway
```

### 3. 运行单个容器

```bash
# 运行 user-service
docker run -d -p 7081:7081 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mydatabase123.mysql.database.azure.com:3306/userdb?useSSL=true&requireSSL=true" \
  -e SPRING_DATASOURCE_USERNAME="ournoevl" \
  -e SPRING_DATASOURCE_PASSWORD="12345678" \
  --name user-service \
  user-service:latest
```

## ☁️ 部署到 Azure Container Instances

### 前置要求

1. 安装 Azure CLI
2. 登录 Azure：`az login`
3. 创建资源组和容器注册表（ACR）

### 部署步骤

```bash
# 1. 构建并推送镜像到 ACR
./build-all.sh

# 2. 部署到 Azure Container Instances
./deploy-all.sh
```

### 手动部署

```bash
# 设置变量
RESOURCE_GROUP=MyWeb
ACR_NAME=myweb2025

# 登录到 ACR
az acr login --name $ACR_NAME

# 部署单个服务
az container create \
  --resource-group $RESOURCE_GROUP \
  --name user-service \
  --image $ACR_NAME.azurecr.io/user-service:latest \
  --cpu 1 --memory 1 --ports 7081 \
  --ip-address Public \
  --registry-login-server $ACR_NAME.azurecr.io \
  --registry-username $ACR_NAME \
  --registry-password $(az acr credential show --name $ACR_NAME --query "passwords[0].value" -o tsv)
```

## 🔧 Dockerfile 说明

每个服务的 Dockerfile 结构：

```dockerfile
FROM eclipse-temurin:17-jre          # 使用 Java 17 JRE
ARG JAR_FILE=target/*.jar             # JAR 文件路径
COPY ${JAR_FILE} /app.jar             # 复制 JAR 到容器
EXPOSE 7081                           # 暴露端口
ENTRYPOINT ["java","-jar","/app.jar"] # 启动命令
```

## 📋 常用命令

### Docker Compose

```bash
# 启动所有服务
docker-compose up -d

# 启动特定服务
docker-compose up -d user-service api-gateway

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f user-service

# 重启服务
docker-compose restart user-service

# 停止所有服务
docker-compose down

# 重新构建并启动
docker-compose up -d --build
```

### Docker 命令

```bash
# 查看运行中的容器
docker ps

# 查看所有容器
docker ps -a

# 查看容器日志
docker logs -f user-service

# 进入容器
docker exec -it user-service sh

# 停止容器
docker stop user-service

# 删除容器
docker rm user-service

# 查看镜像
docker images

# 删除镜像
docker rmi user-service:latest
```

## 🌐 服务访问

容器启动后，服务可以通过以下地址访问：

- API Gateway: http://localhost:7080
- User Service: http://localhost:7081
- Content Service: http://localhost:7082
- Transaction Service: http://localhost:7083
- Admin Service: http://localhost:7084
- Notification Service: http://localhost:7085

## 🔍 故障排查

### 1. 容器无法启动

```bash
# 查看容器日志
docker logs user-service

# 检查容器状态
docker ps -a
```

### 2. 服务无法连接数据库

- 检查数据库连接字符串是否正确
- 确认数据库服务可访问
- 检查网络连接：`docker network ls`

### 3. 端口冲突

```bash
# 查看端口占用
netstat -ano | findstr :7081

# 修改 docker-compose.yml 中的端口映射
ports:
  - "17081:7081"  # 改为其他端口
```

### 4. 镜像构建失败

```bash
# 确保先编译项目
mvn clean package -DskipTests

# 检查 Dockerfile 路径
docker build -t user-service:latest ./user-service
```

## 📝 环境变量

可以通过环境变量覆盖配置：

```bash
# docker-compose.yml 中设置
environment:
  - SPRING_DATASOURCE_URL=jdbc:mysql://...
  - SPRING_DATASOURCE_USERNAME=username
  - SPRING_DATASOURCE_PASSWORD=password
```

## 🎯 最佳实践

1. **开发和生产环境**：统一使用 `docker-compose.yml`（使用 Azure MySQL）
2. **CI/CD**：使用 `build-all.sh` 和 `deploy-all.sh`
3. **监控**：使用 `docker-compose logs` 查看日志
4. **环境变量**：通过 `.env` 文件或环境变量覆盖配置

## 🔐 安全建议

1. 不要在镜像中硬编码密码
2. 使用环境变量或密钥管理服务
3. 定期更新基础镜像
4. 使用非 root 用户运行容器
5. 扫描镜像漏洞：`docker scan user-service:latest`

