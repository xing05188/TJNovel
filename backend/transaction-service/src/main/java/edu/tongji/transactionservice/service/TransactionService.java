package edu.tongji.transactionservice.service;

import edu.tongji.transactionservice.entity.Transaction;
import edu.tongji.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    /**
     * 获取读者的打赏记录
     * @param readerId 读者ID
     * @return 打赏记录列表
     */
    public List<Transaction> getRewardTransactionsByReaderId(Long readerId) {
        return transactionRepository.findRewardTransactionsByReaderId(readerId);
    }
    
    /**
     * 获取读者的订阅记录（解锁章节记录）
     * @param readerId 读者ID
     * @return 订阅记录列表
     */
    public List<Transaction> getSubscriptionTransactionsByReaderId(Long readerId) {
        return transactionRepository.findSubscriptionTransactionsByReaderId(readerId);
    }
    
    /**
     * 获取读者的充值记录
     * @param readerId 读者ID
     * @return 充值记录列表
     */
    public List<Transaction> getRechargeTransactionsByReaderId(Long readerId) {
        return transactionRepository.findRechargeTransactionsByReaderId(readerId);
    }
    
    /**
     * 获取读者的所有交易记录
     * @param readerId 读者ID
     * @return 所有交易记录列表
     */
    public List<Transaction> getAllTransactionsByReaderId(Long readerId) {
        return transactionRepository.findByReaderIdOrderByTimeDesc(readerId);
    }
    
    /**
     * 根据读者ID和交易类型获取交易记录
     * @param readerId 读者ID
     * @param transType 交易类型（充值/打赏/订阅/解锁章节）
     * @return 交易记录列表
     */
    public List<Transaction> getTransactionsByReaderIdAndType(Long readerId, String transType) {
        return transactionRepository.findByReaderIdAndTransTypeOrderByTimeDesc(readerId, transType);
    }
    
    /**
     * 根据读者ID和时间范围获取交易记录
     * @param readerId 读者ID
     * @param timeRange 时间范围（all/year/month/week）
     * @return 交易记录列表
     */
    public List<Transaction> getTransactionsByReaderIdAndTimeRange(Long readerId, String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime;
        
        switch (timeRange != null ? timeRange.toLowerCase() : "all") {
            case "year":
                startTime = now.minus(1, ChronoUnit.YEARS);
                break;
            case "month":
                startTime = now.minus(1, ChronoUnit.MONTHS);
                break;
            case "week":
                startTime = now.minus(1, ChronoUnit.WEEKS);
                break;
            default:
                // all - 返回所有记录
                return transactionRepository.findByReaderIdOrderByTimeDesc(readerId);
        }
        
        return transactionRepository.findByReaderIdAndTimeBetweenOrderByTimeDesc(readerId, startTime, now);
    }
    
    /**
     * 根据读者ID、交易类型和时间范围获取交易记录
     * @param readerId 读者ID
     * @param transType 交易类型
     * @param timeRange 时间范围（all/year/month/week）
     * @return 交易记录列表
     */
    public List<Transaction> getTransactionsByReaderIdAndTypeAndTimeRange(Long readerId, String transType, String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime;
        
        switch (timeRange != null ? timeRange.toLowerCase() : "all") {
            case "year":
                startTime = now.minus(1, ChronoUnit.YEARS);
                break;
            case "month":
                startTime = now.minus(1, ChronoUnit.MONTHS);
                break;
            case "week":
                startTime = now.minus(1, ChronoUnit.WEEKS);
                break;
            default:
                // all - 只按类型过滤
                return transactionRepository.findByReaderIdAndTransTypeOrderByTimeDesc(readerId, transType);
        }
        
        return transactionRepository.findByReaderIdAndTransTypeAndTimeBetweenOrderByTimeDesc(readerId, transType, startTime, now);
    }
    
    /**
     * 获取所有交易记录
     * @return 所有交易记录列表
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    /**
     * 根据ID获取交易记录
     * @param transactionId 交易ID
     * @return 交易记录
     */
    public Optional<Transaction> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }
    
    /**
     * 创建交易记录
     * @param transaction 交易记录
     * @return 创建的交易记录
     */
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
    
    /**
     * 更新交易记录
     * @param transactionId 交易ID
     * @param transaction 更新的交易记录
     * @return 更新后的交易记录
     */
    public Optional<Transaction> updateTransaction(Long transactionId, Transaction transaction) {
        if (!transactionRepository.existsById(transactionId)) {
            return Optional.empty();
        }
        
        transaction.setTransactionId(transactionId);
        return Optional.of(transactionRepository.save(transaction));
    }
    
    /**
     * 删除交易记录
     * @param transactionId 交易ID
     * @return 是否删除成功
     */
    public boolean deleteTransaction(Long transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            return false;
        }
        
        transactionRepository.deleteById(transactionId);
        return true;
    }
}