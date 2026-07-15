package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.entity.Rate;
import edu.tongji.contentservice.entity.RateId;
import edu.tongji.contentservice.repository.NovelRepository;
import edu.tongji.contentservice.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RateService {
    private final RateRepository rateRepository;
    private final NovelRepository novelRepository;
    
    public RateService(RateRepository rateRepository, NovelRepository novelRepository) {
        this.rateRepository = rateRepository;
        this.novelRepository = novelRepository;
    }
    
    @Transactional
    public boolean addRate(Long novelId, Long readerId, BigDecimal score) {
        Optional<Novel> novel = novelRepository.findById(novelId);
        if (novel.isEmpty()) {
            return false; // 小说不存在
        }
        
        // 检查评分是否已存在，如果存在则更新，否则创建新记录
        Rate rate;
        if (rateRepository.existsByNovelIdAndReaderId(novelId, readerId)) {
            Optional<Rate> existingRate = rateRepository.findById(new RateId(novelId, readerId));
            if (existingRate.isPresent()) {
                rate = existingRate.get();
                rate.setRate(score);
                rate.setRateTime(LocalDateTime.now());
            } else {
                return false;
            }
        } else {
            rate = new Rate(novelId, readerId, score, LocalDateTime.now());
        }
        
        try {
            rateRepository.save(rate);
            updateNovelRating(novelId);
            return true;
        } catch (Exception e) {
            // 记录错误并返回失败
            System.err.println("Error saving rate: " + e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean deleteRate(Long novelId, Long readerId) {
        if (!rateRepository.existsByNovelIdAndReaderId(novelId, readerId)) {
            return false; // 评分记录不存在
        }
        
        try {
            RateId rateId = new RateId(novelId, readerId);
            rateRepository.deleteById(rateId);
            updateNovelRating(novelId);
            return true;
        } catch (Exception e) {
            // 记录错误并返回失败
            System.err.println("Error deleting rate: " + e.getMessage());
            return false;
        }
    }
    
    public List<Rate> getRatesByNovelId(Long novelId) {
        return rateRepository.findByNovelId(novelId);
    }
    
    public List<Rate> getRatesByReaderId(Long readerId) {
        return rateRepository.findByReaderId(readerId);
    }
    
    public List<Rate> getAllRates() {
        return rateRepository.findAll();
    }
    
    private void updateNovelRating(Long novelId) {
        List<Rate> rates = rateRepository.findByNovelId(novelId);
        Optional<Novel> novel = novelRepository.findById(novelId);
        
        if (novel.isPresent()) {
            Novel novelEntity = novel.get();
            if (rates.isEmpty()) {
                novelEntity.setScore(BigDecimal.ZERO);
            } else {
                BigDecimal sum = rates.stream()
                    .map(Rate::getRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal average = sum.divide(BigDecimal.valueOf(rates.size()), 2, RoundingMode.HALF_UP);
                novelEntity.setScore(average);
            }
            novelRepository.save(novelEntity);
        }
    }
}