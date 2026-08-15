const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8086,
    historyApiFallback: true,
    proxy: {
      // 通过API Gateway统一入口（7080端口）
      '/api': {
        target: 'http://localhost:7080',
        changeOrigin: true,
        // 不需要pathRewrite，API Gateway会处理路由
      },
      // WebSocket 通知推送（Spring Cloud Gateway 透传 WS 升级握手）
      // 注意：不能代理整个 /ws，否则会劫持 webpack-dev-server HMR 的 /ws 连接
      '/ws/notifications': {
        target: 'ws://localhost:7080',
        changeOrigin: true,
        ws: true
      },
      // MinIO 对象存储（头像/封面等），走 8086 端口避免暴露 9000 端口
      '/minio': {
        target: 'http://localhost:9000',
        changeOrigin: true,
        pathRewrite: { '^/minio': '' }
      }
    }
  }
})
