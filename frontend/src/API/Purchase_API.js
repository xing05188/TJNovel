import request from '@/API/index'

/**
 * 购买章节
 * @param {Object} purchaseData - 购买数据
 * @typedef {Object} PurchaseData
 * @property {number} readerId - 读者ID
 * @property {number} novelId - 小说ID
 * @property {number} chapterId - 章节ID
 * @returns {Promise<Object>} 返回购买结果
 */
export function purchaseChapter(purchaseData) {
    // 验证必需字段
    if (!purchaseData || !purchaseData.readerId || !purchaseData.novelId || !purchaseData.chapterId) {
        return Promise.reject(new Error('购买数据不完整：readerId、novelId 和 chapterId 都是必需的'));
    }
    
    // 确保字段类型正确
    const requestData = {
        readerId: Number(purchaseData.readerId),
        novelId: Number(purchaseData.novelId),
        chapterId: Number(purchaseData.chapterId)
    };
    
    // 验证数字有效性
    if (isNaN(requestData.readerId) || isNaN(requestData.novelId) || isNaN(requestData.chapterId)) {
        return Promise.reject(new Error('购买数据格式错误：readerId、novelId 和 chapterId 必须是有效数字'));
    }
    
    return request({
        url: '/api/Purchase',
        method: 'post',
        headers: {
            'Content-Type': 'application/json'
        },
        data: requestData
    })
}

/**
 * 检查是否已购买章节
 * @param {number} readerId - 读者ID
 * @param {number} novelId - 小说ID
 * @param {number} chapterId - 章节ID
 * @returns {Promise<{success: boolean, hasPurchased: boolean}>} 返回购买状态
 */
export function checkPurchase(readerId, novelId, chapterId) {
    return request({
        url: '/api/Purchase/check',
        method: 'get',
        params: {
            readerId,
            novelId,
            chapterId
        }
    })
}