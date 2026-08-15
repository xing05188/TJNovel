#!/bin/bash
# 启动 TJNovel 后端全部微服务
set -u

BASE=/home/zhx/TJNovel/backend
LOG=/home/zhx/TJNovel/logs
mkdir -p "$LOG"

# 数据库连接凭据（docker-compose 中定义）
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=root123

# RabbitMQ 连接凭据（docker-compose 中定义）
export SPRING_RABBITMQ_HOST=localhost
export SPRING_RABBITMQ_PORT=5672
export SPRING_RABBITMQ_USERNAME=tjnovel
export SPRING_RABBITMQ_PASSWORD=tjnovel123

# 支付宝沙盒配置（默认值在 application.yml 中，环境变量优先级更高）
export ALIPAY_APP_ID="${ALIPAY_APP_ID:-9021000158638440}"
export ALIPAY_PRIVATE_KEY="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCnyAZ4vYiAgjIWByLB8yI9UpXfOfIy5hIOQb+VgViO3DW4wq3tkEHuQuNlJDdmS2cCypvk/mJWfyYhvAgqpQ9JdhzVafVGeD7Xg/hTLMGS04JaPaTFq0Lpy5QuEzr9up4PP3pCpMeSXZNuoD+/x/fXrAcJNHyXbh5SPRBo/YyAAcRjHk3/AtqM34fS+sz/ALCmji7KiSNqj7oRzv4WhOPtJPCJNys9QbrhI0yl67iHb/gQw32G3WdNsoNp1SGEdHhFnWHDhKAKcw0DQxDa89jOPmThKhpS8cDnpUPB1Q+9OScvqNtkFcCeiEHGiygyZiL4tA4WLNRpWr8CpGFMaHyhAgMBAAECggEAe/fASGAKRqFsLnhxRwrFRrgJgplHCjKSLrSHwQiEDXJJw00cw8Xt4QTNDYMX595Yq2hWjXkj4bMq0owjIJc611WM8mbK0pmwHP8mcRZFpJ3g15pqb58d3q8Luot879J+TNfjFvC3gL7hW0DOOX6f8lvqCvQcBqtXff4ebOTCdiUi/s3gNUL9jGR5enV1YDKy65zSxwnUdF323WrpW4eH6NAG/vSrP5zFeXJ0sw5qcaoVs6RUG7rQb39qcbM58hUacFD4E7IVE7pCl1jn1sjPzPg+X4v4D5IpABR8IUCk4cmcZcNqcZX8X+e6VZHMzliE+ptib5vzbIJ5P0WmjmEVXQKBgQDtjDPtxKoHsS9lf1S6ZLk+/x6OQGwBUWXc9aiIeCuUTx46rZJG1FNDiO8kRvtj/MBIIE3c2rHc/GK/EzLZ3M4AtNmww8QV+oOVO55UTWlf8s7lVzjua8ms3KTX8n/6FHVhAG+O8rAMFJkr8ZDhAy//QuCQsnvK/6R8ZjRQPENeHwKBgQC00Hi/cl0/6pdbFekT5lQYMgYrnQK9OebxYJf2NKgpFJTvdQe6ER5bvGtsrvawAzdI9y3EKCe7pCvoQt+xTbTlqOK7q2FPda6r/8UuJ/U9djXD4Ft9cANpZWxtno2CYlZhP3tGrNQefFixJ6gFBnxov8orRpddOf6Na3upUNtNPwKBgHmAWdtG2TUgAr8mdr7z3/CB6y4Vc2jlKnXvTOmolZnBMhQPTZ9e4MvNUYw7pj/JOsxpgYnybkE68oMLHdpjXxbraeFI8JXRUdZV7TDve3NxLTQO/fatOTVqkgJGbgEKz9Lytxzlsvgq3QGmCFWfHMCyg0xfjoQRaB+c4ysa99Y/AoGAV+MI/g1ps/qkDRsOSqTm9UfDZgmQXke5sFPCKWMY5bbiWa4pzlAp0G86S/6TW6SEh8vVjAX0oik/SkrPDhB2QDch1hSMXSIXf/T3c+LqOjCuG4J/Fe6PSfiJhpLNt7VyxRwlIIo7JIJOXfWnnqGzcPrgGr7aLe9Vgone9sKdheMCgYBBrYP6NS0m4ZYjwt0LR6/eGih+TkmcjbWKL65bKHEExrHEBcRgR0PEzTwAmbsSxqI/1u5tdwpU4uXpHxyocKmD4Uj/DAmBTB7OmIE+gjTVeSvJCafsg9qUkqjTqdyvWV0oTETAQnyp8y8/JBh2T5/Kp0HY+MzS/clpHd/pY1P1rQ=="
export ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjXJ+UMbZxe+3aZS8UzGVXrjtNkMEVrmBZb+5sL4ikEfKkbFrClgseI9/imxcTY3w154xSNfKBzZpEJ5w3p4bAVsDTVl2DSOT7qBfzjwi35K3u/zV7O2LmKnC60KSrClqtDQKhqZrKxY3D1YStUyK6uc+r0k9hIIBIRrDptDuWPWG8JeskldB3qG22S7BB/89uucvsLxg2X3DgNMU4QhFwTmWBIfBPsslm6v+epMKPWPtlHx7p9GfF7VBEKgWTcLGaArRnS40ZQkhKZRy0NysN9NGyrywOCG/WUj2TBdYBvW4XuJl+gImB14FghCO7fH1HZ0OegajmMDbxXoM64YdLQIDAQAB"
# export ALIPAY_NOTIFY_URL="http://你的公网地址/api/payment/alipay/notify"
export ALIPAY_NOTIFY_URL="http://20.243.208.35:7080/api/payment/alipay/notify"
# 支付完成后同步回跳的前端页面（PC 网页支付点“返回商家”时跳转）
export ALIPAY_RETURN_URL="http://20.243.208.35:8086/Novels/Novel_Recharge"

# 服务名:可执行jar的相对路径
SERVICES=(
  "api-gateway:api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar"
  "user-service:user-service/target/user-service-0.0.1-SNAPSHOT.jar"
  "content-service:content-service/target/content-service-0.0.1-SNAPSHOT.jar"
  "transaction-service:transaction-service/target/transaction-service-0.0.1-SNAPSHOT.jar"
  "admin-service:admin-service/target/admin-service-0.0.1-SNAPSHOT.jar"
  "notification-service:notification-service/target/notification-service-0.0.1-SNAPSHOT.jar"
)

PIDS=()
for entry in "${SERVICES[@]}"; do
  name="${entry%%:*}"
  jar="${entry#*:}"
  if [ ! -f "$BASE/$jar" ]; then
    echo "[$name] SKIP: jar not found ($jar)"
    continue
  fi
  nohup java -jar "$BASE/$jar" > "$LOG/$name.log" 2>&1 &
  pid=$!
  PIDS+=("$name:$pid")
  echo "[$name] started, PID $pid -> logs/$name.log"
done

echo "---"
echo "All backend services launched. PIDs:"
for p in "${PIDS[@]}"; do echo "  $p"; done
