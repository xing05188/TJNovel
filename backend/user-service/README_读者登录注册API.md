# 读者登录注册API使用说明

## 一、API接口

### 1. 读者注册

**接口地址**：`POST /readers/register`

**请求头**：
```
Content-Type: application/json
```

**请求体**：
```json
{
  "readerName": "reader123",
  "password": "password123",
  "phone": "13800138000"
}
```

**响应示例（成功）**：
```json
{
  "service": "user-service",
  "status": "ok",
  "data": {
    "readerId": 1,
    "readerName": "reader123",
    "phone": "13800138000",
    "balance": 0.00,
    "isCollectVisible": "是",
    "isRecommendVisible": "是",
    "createTime": "2024-01-01T10:00:00"
  },
  "message": "注册成功"
}
```

**响应示例（失败 - 用户名已存在）**：
```json
{
  "service": "user-service",
  "status": "error",
  "data": null,
  "message": "用户名已存在"
}
```

---

### 2. 读者登录

**接口地址**：`POST /readers/login`

**请求头**：
```
Content-Type: application/json
```

**请求体**：
```json
{
  "readerName": "reader123",
  "password": "password123"
}
```

**响应示例（成功）**：
```json
{
  "service": "user-service",
  "status": "ok",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "readerName": "reader123",
    "readerId": 1
  },
  "message": "登录成功"
}
```

**响应示例（失败 - 用户名不存在）**：
```json
{
  "service": "user-service",
  "status": "error",
  "data": null,
  "message": "用户名不存在"
}
```

**响应示例（失败 - 密码错误）**：
```json
{
  "service": "user-service",
  "status": "error",
  "data": null,
  "message": "密码错误"
}
```

---

### 3. 健康检查

**接口地址**：`GET /readers/ping`

**响应示例**：
```json
{
  "service": "user-service",
  "status": "ok",
  "data": "hello"
}
```

---

## 二、JWT Token使用

### 1. Token格式
登录成功后，响应中的 `token` 字段即为JWT Token，格式如下：
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWFkZXIxMjMiLCJyZWFkZXJJZCI6MSwiaWF0IjoxNzA0MDk2MDAwLCJleHAiOjE3MDQxMDMyMDB9.signature
```

### 2. Token包含的信息
- `readerName`：读者用户名
- `readerId`：读者ID
- `iss`：签发者（tj-novel）
- `aud`：受众（tj-novel）
- `exp`：过期时间（2小时后）

### 3. Token使用方式
在需要认证的接口中，需要在请求头中携带Token：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4. Token有效期
Token有效期为2小时，过期后需要重新登录。

---

## 三、密码加密说明

密码使用PBKDF2算法加密，与原Backend保持一致：
- 算法：PBKDF2WithHmacSHA256
- Salt：固定值 "FixedSalt123456"
- 迭代次数：10000次
- 密钥长度：120位（15字节）
- 输出格式：Base64编码，截取前20个字符

---

## 四、配置说明

### application.yml配置
```yaml
jwt:
  issuer: tj-novel          # JWT签发者
  audience: tj-novel        # JWT受众
  key: your-256-bit-secret-key-here-must-be-at-least-32-characters-long  # JWT签名密钥（至少32字符）
  expiration: 7200000        # Token过期时间（毫秒），2小时 = 7200000
```

**重要**：生产环境请修改 `jwt.key` 为安全的随机字符串！

---

## 五、测试示例

### 使用curl测试

#### 1. 注册读者
```bash
curl -X POST http://localhost:7081/readers/register \
  -H "Content-Type: application/json" \
  -d '{
    "readerName": "testreader",
    "password": "test123",
    "phone": "13800138000"
  }'
```

#### 2. 登录
```bash
curl -X POST http://localhost:7081/readers/login \
  -H "Content-Type: application/json" \
  -d '{
    "readerName": "testreader",
    "password": "test123"
  }'
```

#### 3. 使用Token访问受保护接口（示例）
```bash
curl -X GET http://localhost:7081/readers/{id} \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 六、Swagger文档

启动服务后，访问Swagger UI：
```
http://localhost:7081/swagger-ui.html
```

可以查看完整的API文档和进行在线测试。

---

## 七、注意事项

1. **JWT密钥安全**：生产环境必须使用强随机密钥，不要使用默认密钥
2. **密码安全**：密码在传输时应使用HTTPS加密
3. **Token存储**：前端应安全存储Token（如HttpOnly Cookie或安全的本地存储）
4. **Token过期处理**：前端应处理Token过期情况，自动跳转到登录页面
5. **错误处理**：所有错误信息都会在响应中返回，前端应妥善处理

---

## 八、与原Backend的兼容性

本实现与原.NET Backend完全兼容：
- ✅ 相同的密码加密算法（PBKDF2）
- ✅ 相同的JWT Token格式
- ✅ 相同的API响应格式
- ✅ 相同的错误处理逻辑

可以直接替换原Backend的读者登录注册功能。

