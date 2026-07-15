# 项目常用命令总结

## 📦 构建和启动

### PowerShell 脚本（Windows）

#### 1. 构建 Docker 镜像
```powershell
.\build-local.ps1
```
- 编译 Maven 项目
- 检查并拉取基础镜像
- 构建所有服务的 Docker 镜像
- 显示 Swagger UI 访问地址

#### 2. 启动所有服务（推荐）
```powershell
.\start-services.ps1
```
- 自动检查 Docker 是否运行
- 检查端口冲突
- 如果发现其他项目容器占用端口，会提示是否停止
- 只启动项目内的容器

或直接使用 docker-compose：
```powershell
docker-compose up -d
```
或带构建：
```powershell
docker-compose up -d --build
```

#### 3. 停止所有服务
```powershell
.\stop-services.ps1
```
- 只停止项目内的容器，不影响其他项目

或直接使用：
```powershell
docker-compose down
```

#### 4. 显示 Swagger UI 地址
```powershell
.\show-swagger-urls.ps1
```

---

## 🐳 Docker Compose 命令

### 基本操作

#### 启动服务
```powershell
# 后台启动所有服务
docker-compose up -d

# 前台启动（查看日志）
docker-compose up

# 启动并重新构建镜像
docker-compose up -d --build

# 启动特定服务
docker-compose up -d api-gateway user-service
```

#### 停止服务
```powershell
# 停止并删除容器
docker-compose down

# 停止并删除容器、网络和卷
docker-compose down -v

# 强制停止并删除
docker-compose down --remove-orphans
```

#### 重启服务
```powershell
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart user-service
```

#### 查看状态
```powershell
# 查看运行中的服务
docker-compose ps

# 查看所有服务（包括停止的）
docker-compose ps -a
```

---

## 📋 日志查看

### 查看日志
```powershell
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f user-service

# 查看最近 100 行日志
docker-compose logs --tail=100 user-service

# 查看特定服务的最后 50 行日志
docker-compose logs --tail=50 -f api-gateway
```

### 单独使用 Docker 命令
```powershell
# 查看容器日志
docker logs api-gateway

# 实时查看日志
docker logs -f user-service

# 查看最近 100 行
docker logs --tail=100 content-service
```

---

## 🔍 容器管理

### 查看容器
```powershell
# 查看运行中的容器
docker ps

# 查看所有容器（包括停止的）
docker ps -a

# 查看项目相关的容器
docker ps --filter "name=api-gateway" --filter "name=user-service"
```

### 进入容器
```powershell
# 进入容器（交互式 shell）
docker exec -it api-gateway sh

# 进入容器（bash，如果支持）
docker exec -it user-service bash
```

### 容器操作
```powershell
# 停止容器
docker stop api-gateway

# 启动容器
docker start api-gateway

# 重启容器
docker restart user-service

# 删除容器
docker rm api-gateway

# 强制删除运行中的容器
docker rm -f user-service
```

---

## 🖼️ 镜像管理

### 查看镜像
```powershell
# 查看所有镜像
docker images

# 查看项目相关镜像
docker images | Select-String "ournovel"

# 查看特定镜像
docker images ournovel/api-gateway:local
```

### 删除镜像
```powershell
# 删除特定镜像
docker rmi ournovel/api-gateway:local

# 强制删除镜像
docker rmi -f ournovel/user-service:local

# 删除所有未使用的镜像
docker image prune -a
```

---

## 🧪 测试命令

### API 测试
```powershell
# 测试 API Gateway
curl http://localhost:7080/gateway/ping

# 测试 User Service
curl http://localhost:7080/users/ping

# 测试 Content Service
curl http://localhost:7080/content/ping

# 测试 Transaction Service
curl http://localhost:7080/transactions/ping

# 测试 Admin Service
curl http://localhost:7080/admin/ping
```

### 直接访问服务（绕过 Gateway）
```powershell
# User Service
curl http://localhost:7081/users/ping

# Content Service
curl http://localhost:7082/content/ping

# Transaction Service
curl http://localhost:7083/transactions/ping

# Admin Service
curl http://localhost:7084/admin/ping

# Notification Service
curl http://localhost:7085/notify/ping
```

---

## 📚 Swagger UI 访问

### 直接访问各服务
- User Service: `http://localhost:7081/swagger-ui.html`
- Content Service: `http://localhost:7082/swagger-ui.html`
- Transaction Service: `http://localhost:7083/swagger-ui.html`
- Admin Service: `http://localhost:7084/swagger-ui.html`
- Notification Service: `http://localhost:7085/swagger-ui.html`
- API Gateway: `http://localhost:7080/swagger-ui.html`

---

## 🛠️ Maven 命令

### 编译项目
```powershell
# 编译所有模块（跳过测试）
mvn package -DskipTests

# 清理并编译
mvn clean package -DskipTests

# 只编译特定模块
mvn package -pl user-service -am -DskipTests
```

### 安装依赖
```powershell
# 安装所有依赖
mvn install -DskipTests

# 只安装依赖，不编译
mvn dependency:resolve
```

---

## 🧹 清理命令

### 清理 Docker 资源
```powershell
# 停止并删除所有容器、网络
docker-compose down

# 删除所有未使用的容器
docker container prune

# 删除所有未使用的镜像
docker image prune -a

# 删除所有未使用的卷
docker volume prune

# 清理所有未使用的资源
docker system prune -a
```

### 清理 Maven
```powershell
# 清理所有模块的 target 目录
mvn clean
```

---

## 📊 监控和调试

### 查看资源使用
```powershell
# 查看容器资源使用情况
docker stats

# 查看特定容器资源使用
docker stats api-gateway user-service
```

### 查看网络
```powershell
# 查看 Docker 网络
docker network ls

# 查看项目网络详情
docker network inspect ournovel2_microservices-network
```

### 查看端口占用
```powershell
# Windows 查看端口占用
netstat -ano | findstr :7080

# 查看所有服务端口
docker-compose ps
```

---

## 🚀 快速工作流

### 完整重启流程
```powershell
# 1. 停止所有服务
docker-compose down

# 2. 重新构建镜像
.\build-local.ps1

# 3. 启动所有服务
docker-compose up -d

# 4. 查看日志
docker-compose logs -f
```

### 快速开发流程
```powershell
# 1. 修改代码后重新编译
mvn package -DskipTests

# 2. 重新构建并启动
docker-compose up -d --build

# 3. 查看特定服务日志
docker-compose logs -f user-service
```

---

## 📝 服务端口列表

| 服务 | 端口 | 说明 |
|------|------|------|
| API Gateway | 7080 | 网关服务 |
| User Service | 7081 | 用户服务 |
| Content Service | 7082 | 内容服务 |
| Transaction Service | 7083 | 交易服务 |
| Admin Service | 7084 | 管理服务 |
| Notification Service | 7085 | 通知服务 |

---

## 💡 常用组合命令

### 一键重启特定服务
```powershell
docker-compose restart user-service && docker-compose logs -f user-service
```

### 查看所有服务健康状态
```powershell
docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
```

### 批量操作
```powershell
# 停止所有服务并清理
docker-compose down && docker system prune -f
```

---

## ⚠️ 注意事项

1. **端口冲突**：确保端口 7080-7085 未被占用
2. **数据库连接**：确保 Azure MySQL 数据库可访问
3. **Docker Desktop**：确保 Docker Desktop 正在运行
4. **网络问题**：如果无法拉取镜像，检查网络和代理设置

---

## 🔗 相关文件

- `build-local.ps1` - 构建脚本
- `stop-services.ps1` - 停止服务脚本
- `show-swagger-urls.ps1` - 显示 Swagger UI 地址
- `docker-compose.yml` - Docker Compose 配置
- `test-apis.ps1` - API 测试脚本（如果存在）

