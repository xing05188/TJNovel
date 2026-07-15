import request from '@/API/index'

/**
 * 上传用户头像到OSS并更新数据库
 * @param {number} authorId - 作者ID
 * @param {File} avatarFile - 头像图片文件对象
 * @returns {Promise<string>} 返回头像的完整OSS URL
 * @throws {Error} 当上传失败或参数无效时抛出错误
 */
export function uploadAuthorAvatar(authorId, avatarFile) {
    if (!authorId) {
        return Promise.reject(new Error('作者ID不能为空'));
    }
    
    if (!avatarFile) {
        return Promise.reject(new Error('请选择要上传的头像文件'));
    }
    
    const formData = new FormData();
    // 后端期望参数名为 "file"，不是 "avatarFile"
    formData.append('file', avatarFile); 

    return request({
        // 注意：后端代码使用了 @PathVariable Long id，但 @PostMapping("/UploadAvatar") 路径中没有 {id}
        // 这可能是后端代码的错误。正确的路径应该是 /authors/{id}/UploadAvatar
        // 如果这个路径仍然返回 404，可能需要修复后端代码，将 @PostMapping("/UploadAvatar") 
        // 改为 @PostMapping("/{id}/UploadAvatar")
        url: `/api/Author/${authorId}/UploadAvatar`, 
        method: 'post',
        data: formData,
        // 不设置 Content-Type，让浏览器自动设置（包括 boundary）
        // axios 会自动为 FormData 设置正确的 Content-Type
    }).then(res => {
        // 响应拦截器已经处理了 ApiResponse 格式，返回的是 data 字段的值
        // 如果返回的是字符串（URL），直接返回；如果是对象，尝试提取 URL
        if (typeof res === 'string') {
            return res;
        } else if (res && typeof res === 'object') {
            return res.avatarUrl || res.url || res.data || res;
        }
        return res;
    });
}