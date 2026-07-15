import axios from 'axios'

// 创建 axios 实例
// 直接使用 API Gateway 地址（方案二）
const service = axios.create({
    baseURL: process.env.VUE_APP_BASE_API || 'http://4.233.147.12:7080', // 从环境变量获取，默认使用 API Gateway
    timeout: 10000 // 请求超时时间（10秒）
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        // 添加 token 到请求头
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        // 确保PUT和POST请求的data不会被转换为查询参数
        // 只有当有data且没有params时，才处理data为JSON body
        if ((config.method === 'put' || config.method === 'post') && config.data && !config.params) {
            // 对于 FormData 或 URLSearchParams，让浏览器/axios 自动设置 Content-Type
            if (!(config.data instanceof FormData) && !(config.data instanceof URLSearchParams)) {
                // 确保Content-Type正确设置（仅对非 FormData 的请求）
            if (!config.headers['Content-Type']) {
                config.headers['Content-Type'] = 'application/json';
            }
            // 确保data是对象时被正确序列化为JSON
                if (typeof config.data === 'object' && typeof config.data !== 'string') {
                    config.data = JSON.stringify(config.data);
                }
            }
        }
        // 如果POST请求只有params（查询参数），保留params，不设置Content-Type
        // 这样axios会将params作为查询参数发送
        return config
    },
    error => {
        console.log(error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        // 检查是否是XML响应
        const contentType = response.headers?.['content-type'];
        const isXml = typeof contentType === 'string' && (contentType.includes('application/xml') || contentType.includes('text/xml'));
        if (isXml) {
            // 使用DOMParser解析XML
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(response.data, 'text/xml');
            
            // 检查解析错误
            const parserError = xmlDoc.querySelector('parsererror');
            if (parserError) {
                console.error('XML解析错误:', parserError.textContent);
                return Promise.reject(new Error('XML解析错误'));
            }
            
            // 解析ApiResponse格式
            const apiResponse = xmlDoc.querySelector('ApiResponse');
            if (apiResponse) {
                const status = apiResponse.querySelector('status')?.textContent;
                const dataElement = apiResponse.querySelector('data');
                const message = apiResponse.querySelector('message')?.textContent;
                
                // 构建响应数据对象
                const parsedData = {
                    status: status,
                    message: message,
                    data: {}
                };
                
                // 解析data内容
                if (dataElement) {
                    const authorId = dataElement.querySelector('authorId')?.textContent;
                    if (authorId) {
                        parsedData.data.authorId = parseInt(authorId);
                    }
                }
                
                // 如果是错误响应，抛出错误
                if (status === 'error') {
                    const error = new Error(message || '请求失败');
                    error.response = response;
                    return Promise.reject(error);
                }
                
                // 成功响应，返回data字段
                return parsedData.data;
            }
        }
        
        // 对响应数据做处理
        const data = response.data;
        
        // 处理新的API响应格式（包含service, status, data, message）
        if (data && typeof data === 'object' && 'status' in data && 'data' in data) {
            // 如果是错误响应，抛出错误
            if (data.status === 'error') {
                const error = new Error(data.message || '请求失败');
                error.response = response;
                return Promise.reject(error);
            }
            // 成功响应，返回data字段
            return data.data;
        }
        
        // 处理推荐API的响应格式（包含success, message）
        if (data && typeof data === 'object' && 'success' in data) {
            // 如果success为false，抛出错误
            if (data.success === false) {
                const error = new Error(data.message || '操作失败');
                error.response = response;
                return Promise.reject(error);
            }
            // 成功响应，返回整个data对象
            return data;
        }
        
        // 兼容旧格式，直接返回data
        return data;
    },
    error => {
        console.log('Error:', error.response)
        
        // 处理错误响应格式
        if (error.response && error.response.data) {
            const errorData = error.response.data;
            
            // 处理推荐API的错误格式（包含success, message）
            if (errorData.success === false && errorData.message) {
                error.message = errorData.message;
            }
            // 处理新的API响应格式（包含status, message）
            else if (errorData.status === 'error' && errorData.message) {
                error.message = errorData.message;
            }
            // 处理其他可能的错误格式
            else if (errorData.message) {
                error.message = errorData.message;
            }
        }
        
        return Promise.reject(error)
    }
)

export default service