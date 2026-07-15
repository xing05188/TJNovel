package edu.tongji.common.dto;

public record ApiResponse<T>(String service, String status, T data, String message) {
    /**
     * 创建成功响应（带服务名）
     * @param service 服务名称
     * @param data 响应数据
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ok(String service, T data) {
        return new ApiResponse<>(service, "ok", data, null);
    }
    
    /**
     * 创建成功响应（带消息，默认服务名为user-service）
     * @param message 响应消息
     * @param data 响应数据
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> okWithMessage(String message, T data) {
        return new ApiResponse<>("user-service", "ok", data, message);
    }
    
    /**
     * 创建成功响应（带服务名和消息）
     * @param service 服务名称
     * @param message 响应消息
     * @param data 响应数据
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ok(String service, String message, T data) {
        return new ApiResponse<>(service, "ok", data, message);
    }
    
    /**
     * 创建错误响应
     * @param message 错误消息
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("user-service", "error", null, message);
    }
    
    /**
     * 创建错误响应（带服务名）
     * @param service 服务名称
     * @param message 错误消息
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> error(String service, String message) {
        return new ApiResponse<>(service, "error", null, message);
    }
}

