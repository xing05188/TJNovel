#!/usr/bin/env bash
# TJNovel 新电脑环境一键安装脚本
# 用法（在已配置免密码 sudo 的环境下运行）：
#   chmod +x setup-env.sh && ./setup-env.sh
set -e

echo "==> [1/4] 更新 apt 并安装 JDK17 + Node.js + npm"
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk-headless nodejs npm

echo "==> [2/4] 安装 Docker"
if ! command -v docker >/dev/null 2>&1; then
  curl -fsSL https://get.docker.com -o /tmp/get-docker.sh
  sudo sh /tmp/get-docker.sh
  sudo systemctl enable --now docker
  # 让当前用户无需 sudo 即可用 docker
  sudo usermod -aG docker "$USER"
fi

echo "==> 验证版本"
java -version
node -v
npm -v
docker --version
docker compose version

echo "==> 完成。请重新登录或执行 'newgrp docker' 使 docker 组生效。"
