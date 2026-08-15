#!/bin/bash
# 重新打包 user/content/admin 服务并重启
set -x
BASE=/home/zhx/TJNovel/backend
LOG=/home/zhx/TJNovel/logs

echo "[$(date +%T)] stopping old services..."
for svc in user-service content-service admin-service; do
  pkill -f "$svc/target/$svc-0.0.1-SNAPSHOT.jar" 2>/dev/null
done
sleep 3

echo "[$(date +%T)] rebuilding..."
cd "$BASE" && ./mvnw package -DskipTests -q -pl user-service,content-service,admin-service -am > "$LOG/rebuild3.log" 2>&1
if [ $? -ne 0 ]; then echo "[$(date +%T)] BUILD FAILED"; tail -20 "$LOG/rebuild3.log"; exit 1; fi

echo "[$(date +%T)] build OK, restarting services..."
export SPRING_DATASOURCE_USERNAME=root SPRING_DATASOURCE_PASSWORD=root123
for svc in user-service content-service admin-service; do
  nohup java -jar "$BASE/$svc/target/$svc-0.0.1-SNAPSHOT.jar" > "$LOG/$svc.log" 2>&1 &
  echo "[$(date +%T)] $svc started, PID $!"
done
echo "[$(date +%T)] ALL DONE"
