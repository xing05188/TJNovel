package edu.tongji.transactionservice.service;

import edu.tongji.transactionservice.entity.AuthorIncome;
import edu.tongji.transactionservice.repository.AuthorIncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作者收入服务
 */
@Service
public class AuthorIncomeService {
    
    @Autowired
    private AuthorIncomeRepository authorIncomeRepository;
    
    /**
     * 获取作者的收入记录
     * @param authorId 作者ID
     * @return 收入记录列表
     */
    public List<AuthorIncome> getIncomeRecordsByAuthorId(Long authorId) {
        return authorIncomeRepository.findByAuthorIdOrderByCreateTimeDesc(authorId);
    }
    
    /**
     * 获取作者的总收入
     * @param authorId 作者ID
     * @return 总收入
     */
    public BigDecimal getTotalIncomeByAuthorId(Long authorId) {
        return authorIncomeRepository.getTotalIncomeByAuthorId(authorId);
    }
    
    /**
     * 创建作者收入记录
     * @param authorIncome 作者收入记录
     * @return 创建的作者收入记录
     */
    @Transactional
    public AuthorIncome createAuthorIncome(AuthorIncome authorIncome) {
        return authorIncomeRepository.save(authorIncome);
    }
}