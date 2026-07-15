import request from '@/API/index'

/**
 * 发起充值
 * @param {Object} rechargeData - 充值数据
 * @param {number} rechargeData.ReaderId - 用户ID
 * @param {number} rechargeData.Amount - 充值金额
 * @returns {Promise<Object>} 返回支付URL
 */
export function startRecharge(rechargeData) {
    return request({
        url: '/api/Recharge/start',
        method: 'post',
        headers: {
            'Content-Type': 'application/json'
        },
        data: {
            readerId: rechargeData.ReaderId || rechargeData.readerId,
            amount: rechargeData.Amount || rechargeData.amount
        }
    })
}