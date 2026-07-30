const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8086,
    proxy: {
      // 通过API Gateway统一入口（7080端口）
      '/api': {
        target: 'http://4.233.147.12:7080',
        changeOrigin: true,
        // 不需要pathRewrite，API Gateway会处理路由
      },
      // WebSocket 通知推送（Spring Cloud Gateway 透传 WS 升级握手）
      '/ws': {
        target: 'ws://4.233.147.12:7080',
        changeOrigin: true,
        ws: true
      }
    }
  }
})
