# TJNovel — 小说平台微服务系统

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3.3.5"/>
  <img src="https://img.shields.io/badge/Spring_Cloud-2023.0.3-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Cloud 2023.0.3"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL 8.0"/>
  <img src="https://img.shields.io/badge/Redis-7.2-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis 7.2"/>
  <img src="https://img.shields.io/badge/RabbitMQ-3.13-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ 3.13"/>
  <img src="https://img.shields.io/badge/Elasticsearch-8.12-005571?style=for-the-badge&logo=elasticsearch&logoColor=white" alt="Elasticsearch 8.12"/>
  <img src="https://img.shields.io/badge/MinIO--FF0000?style=for-the-badge&logo=minio&logoColor=white" alt="MinIO"/>
  <img src="https://img.shields.io/badge/MyBatis_Plus-3.5.7-003B57?style=for-the-badge&logo=mybatis&logoColor=white" alt="MyBatis-Plus 3.5.7"/>
  <img src="https://img.shields.io/badge/Vue_3-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white" alt="Vue 3"/>
  <img src="https://img.shields.io/badge/Element_Plus-409EFF?style=for-the-badge&logo=element&logoColor=white" alt="Element Plus"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>
</p>

TJNovel 是一个基于微服务架构的小说阅读与管理平台，提供小说发布、章节管理、全文搜索、用户认证、支付交易、评论互动等完整功能。项目采用 Java 17 + Spring Boot 3.3.5 + Spring Cloud 2023.0.3 构建后端，Vue 3 + Element Plus 构建前端，并集成 Docker 容器化部署。

---

## 项目架构

### 系统架构图

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Vue 3 前端  │ ──▶ │ API Gateway  │ ──▶ │  微服务集群  │
│ (Element Plus)│     │  (Port 7080) │     │             │
└─────────────┘     └──────┬───────┘     └──────┬───────┘
                           │                     │
                           │        ┌────────────┼────────────┐
                           │        │            │            │
                     ┌─────┴──┐ ┌──┴────┐ ┌─────┴──┐ ┌──────┴──┐
                     │ User   │ │Content│ │Transaction│ │ Admin  │
                     │ Service│ │Service│ │ Service  │ │ Service│
                     │ :7081  │ │:7082  │ │ :7083    │ │ :7084  │
                     └───┬────┘ └───┬───┘ └────┬─────┘ └───┬────┘
                         │          │          │            │
                    ┌────┴──────────┴──────────┴────────────┴───┐
                    │          Notification Service :7085        │
                    └────────────────────────────────────────────┘
```

### 基础设施

```
┌──────────┐  ┌────────┐  ┌───────────┐  ┌──────────┐  ┌───────┐
│  MySQL 8 │  │ Redis  │  │ RabbitMQ  │  │Elasticsearch│  │ MinIO │
│  数据库  │  │  缓存  │  │  消息队列 │  │  搜索引擎  │  │对象存储│
└──────────┘  └────────┘  └───────────┘  └──────────┘  └───────┘
```

---

## 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 运行环境 |
| Spring Boot | 3.3.5 | 应用框架 |
| Spring Cloud | 2023.0.3 | 微服务治理 |
| Spring Cloud Gateway | — | API 网关 |
| Spring Security | — | 认证授权 |
| Spring Data JPA | — | ORM 基础框架 |
| MyBatis-Plus | 3.5.7 | ORM 增强框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.2 | 缓存 / 分布式锁 |
| Redisson | 3.33.0 | 分布式锁 / 高级 Redis |
| RabbitMQ | 3.13 | 消息队列 |
| Elasticsearch | 8.12.0 | 全文检索 |
| MinIO | — | 对象存储（头像/封面） |
| SpringDoc OpenAPI | 2.6.0 | API 文档 |
| JJWT | 0.12.3 | JWT 认证 |
| Maven | — | 构建工具 |

### 前端

| 技术 | 用途 |
|------|------|
| Vue 3 | 前端框架 |
| Element Plus | UI 组件库 |
| Vue Router 4 | 路由管理 |
| Pinia | 状态管理 |
| Axios | HTTP 请求 |
| ECharts | 数据可视化 |
| Font Awesome | 图标库 |

---

## 微服务模块

| 服务 | 端口 | 说明 |
|------|------|------|
| **api-gateway** | 7080 | 统一入口，路由分发，CORS 跨域处理 |
| **user-service** | 7081 | 用户注册/登录、JWT 认证、头像上传、个人信息管理 |
| **content-service** | 7082 | 小说/章节 CRUD、分类、收藏、评论、评分、排行榜、全文检索 |
| **transaction-service** | 7083 | 充值、打赏、章节购买、整本购买、作者收入 |
| **admin-service** | 7084 | 管理员后台、小说/章节/评论/举报审核管理 |
| **notification-service** | 7085 | 消息通知、异步事件处理（RabbitMQ） |
| **common** | — | 公共模块：DTO、工具类、存储服务抽象 |

---

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- Docker & Docker Compose
- Node.js 18+
- npm / yarn

### 1. 启动基础设施

```bash
cd backend
docker-compose up -d mysql redis rabbitmq elasticsearch minio
```

### 2. 启动后端服务

```bash
cd backend

# 编译项目
mvn clean package -DskipTests

# 构建 Docker 镜像
docker-compose build

# 启动所有服务
docker-compose up -d
```

或使用本地开发模式：

```bash
# 分别启动各微服务（需要先启动基础设施）
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl content-service
mvn spring-boot:run -pl transaction-service
mvn spring-boot:run -pl admin-service
mvn spring-boot:run -pl notification-service
mvn spring-boot:run -pl api-gateway
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run serve
```

### 4. 访问服务

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:8080 |
| API 网关 | http://localhost:7080 |
| Swagger 文档 | http://localhost:7080/swagger-ui.html |
| RabbitMQ 管理 | http://localhost:15672 |
| MinIO 控制台 | http://localhost:9001 |
| Kibana | http://localhost:5601 |

---

## 配置说明

### 环境变量

项目支持通过环境变量覆盖默认配置，关键变量如下：

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `MYSQL_HOST` | localhost | MySQL 主机地址 |
| `MYSQL_ROOT_PASSWORD` | root123 | MySQL root 密码 |
| `REDIS_HOST` | localhost | Redis 主机地址 |
| `RABBITMQ_USER` | tjnovel | RabbitMQ 用户名 |
| `RABBITMQ_PASSWORD` | tjnovel123 | RabbitMQ 密码 |
| `ELASTICSEARCH_HOST` | http://localhost | ES 主机地址 |
| `ELASTICSEARCH_PORT` | 9200 | ES 端口 |
| `MINIO_ENDPOINT` | http://localhost:9000 | MinIO 端点 |
| `MINIO_ACCESS_KEY` | minioadmin | MinIO 访问密钥 |
| `MINIO_SECRET_KEY` | minioadmin123 | MinIO 密钥 |
| `MINIO_BUCKET` | tjnovel | MinIO 默认桶 |

### 数据库初始化

系统启动时会自动执行 `backend/init-db.sql` 创建各微服务所需的数据库：

```sql
-- 创建各微服务数据库
CREATE DATABASE IF NOT EXISTS userdb;
CREATE DATABASE IF NOT EXISTS contentdb;
CREATE DATABASE IF NOT EXISTS transactiondb;
CREATE DATABASE IF NOT EXISTS admindb;
```

表结构由 JPA `ddl-auto` 机制自动生成。

---

## Docker 部署

### 使用 Docker Compose（推荐）

```bash
cd backend

# 构建并启动所有服务
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

### 构建脚本

```bash
# Windows PowerShell
.\build-local.ps1

# Linux / macOS
./build-local.sh
```

详细部署说明请参考 [Docker 部署指南](backend/DOCKER_GUIDE.md)。

---

## API 文档

各微服务通过 SpringDoc OpenAPI 自动生成 Swagger 文档：

| 服务 | Swagger UI |
|------|-----------|
| API Gateway | http://localhost:7080/swagger-ui.html |
| User Service | http://localhost:7081/swagger-ui.html |
| Content Service | http://localhost:7082/swagger-ui.html |
| Transaction Service | http://localhost:7083/swagger-ui.html |
| Admin Service | http://localhost:7084/swagger-ui.html |
| Notification Service | http://localhost:7085/swagger-ui.html |

详细 API 接口列表请参考 [API 文档](backend/README.md)。

---

## 功能特性

### 读者端
- 小说浏览与搜索（支持 Elasticsearch 全文检索）
- 按分类、排行榜浏览小说
- 小说收藏与评分
- 章节阅读与评论互动
- 充值、打赏、章节购买、整本购买
- 个人书架、阅读历史、消息中心

### 作者端
- 小说创建、编辑、发布
- 章节管理（上传、编辑、删除）
- 收入统计与查看
- 读者评论管理
- 作品数据看板

### 管理员端
- 小说审核与管理
- 章节审核与内容管理
- 评论审核与举报处理
- 用户管理
- 数据统计看板

---

## 项目结构

```
TJNovel/
├── backend/                          # 后端项目
│   ├── api-gateway/                  # API 网关服务
│   ├── user-service/                 # 用户服务
│   ├── content-service/              # 内容服务
│   ├── transaction-service/          # 交易服务
│   ├── admin-service/                # 管理服务
│   ├── notification-service/         # 通知服务
│   ├── common/                       # 公共模块
│   ├── docker-compose.yml            # Docker Compose 编排
│   ├── init-db.sql                   # 数据库初始化脚本
│   ├── pom.xml                       # 父 POM 配置
│   ├── DOCKER_GUIDE.md               # Docker 部署指南
│   ├── COMMANDS.md                   # 常用命令参考
│   └── README.md                     # API 文档
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── API/                      # API 接口封装
│   │   ├── Admin/                    # 管理员页面
│   │   ├── Author/                   # 作者页面
│   │   ├── Login_Register/           # 登录注册
│   │   ├── Novels/                   # 小说浏览
│   │   ├── Reader/                   # 读者中心
│   │   ├── router/                   # 路由配置
│   │   └── stores/                   # 状态管理
│   ├── package.json
│   └── vue.config.js
├── docs/                             # 项目文档
│   └── plans.md                      # 技术栈扩展规划
└── README.md                         # 本文件
```

---

## 开发命令

### 后端

```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package -DskipTests

# 启动单个服务
mvn spring-boot:run -pl user-service
```

### 前端

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 生产构建
npm run build

# 代码检查
npm run lint
```

### Docker

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f <service-name>

# 重启服务
docker-compose restart <service-name>

# 清理
docker-compose down -v
```

更多命令请参考 [COMMANDS.md](backend/COMMANDS.md)。

---

## 开发规范

- **代码风格**：遵循阿里巴巴 Java 开发手册
- **提交规范**：提交信息采用 `[type]: description` 格式
- **分支管理**：`main` 为主分支，`develop` 为开发分支
- **API 设计**：遵循 RESTful 风格，统一返回 `ApiResponse` 格式

---

## 许可证

本项目仅供学习交流使用。