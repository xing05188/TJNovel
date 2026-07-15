package edu.tongji.userservice.service;

import edu.tongji.common.service.StorageService;
import edu.tongji.userservice.dto.LoginResponseDto;
import edu.tongji.userservice.entity.Reader;
import edu.tongji.userservice.repository.ReaderRepository;
import edu.tongji.userservice.util.JwtUtil;
import edu.tongji.userservice.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 读者服务类
 */
@Service
public class ReaderService {
    
    @Autowired
    private ReaderRepository readerRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private StorageService storageService;
    
    /**
     * 读者注册
     * @param readerName 用户名
     * @param password 密码
     * @param phone 手机号
     * @return 注册结果
     */
    @Transactional
    public Reader register(String readerName, String password, String phone) {
        // 检查用户名是否已存在
        if (readerRepository.existsByReaderName(readerName)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        // 创建读者对象
        Reader reader = new Reader();
        reader.setReaderName(readerName);
        reader.setPassword(hashedPassword);
        reader.setPhone(phone);
        reader.setBalance(BigDecimal.ZERO);
        reader.setIsCollectVisible("是");
        reader.setIsRecommendVisible("是");
        
        // 保存到数据库
        return readerRepository.save(reader);
    }
    
    /**
     * 读者登录
     * @param readerName 用户名
     * @param password 密码
     * @return 登录响应（包含JWT Token）
     */
    public LoginResponseDto login(String readerName, String password) {
        // 查找读者
        Optional<Reader> readerOpt = readerRepository.findByReaderName(readerName);
        if (readerOpt.isEmpty()) {
            throw new RuntimeException("用户名不存在");
        }
        
        Reader reader = readerOpt.get();
        
        // 验证密码
        if (!PasswordHasher.verifyPassword(password, reader.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(readerName, reader.getReaderId());
        
        // 返回登录响应
        return new LoginResponseDto(token, reader.getReaderName(), reader.getReaderId());
    }
    
    /**
     * 重置密码
     * @param readerName 用户名
     * @param phone 手机号
     * @param newPassword 新密码
     * @return 重置结果
     */
    @Transactional
    public Reader resetPassword(String readerName, String phone, String newPassword) {
        // 查找读者
        Optional<Reader> readerOpt = readerRepository.findByReaderName(readerName);
        if (readerOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        Reader reader = readerOpt.get();
        
        // 验证手机号
        if (!phone.equals(reader.getPhone())) {
            throw new RuntimeException("手机号码不匹配");
        }
        
        // 加密新密码
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        
        // 更新密码
        reader.setPassword(hashedPassword);
        
        // 保存到数据库
        return readerRepository.save(reader);
    }
    
    /**
     * 获取所有读者
     * @return 所有读者列表
     */
    public List<Reader> getAllReaders() {
        return readerRepository.findAll();
    }
    
    /**
     * 获取读者余额
     * @param readerId 读者ID
     * @return 读者余额
     */
    public BigDecimal getBalanceById(Long readerId) {
        Optional<Reader> readerOpt = readerRepository.findById(readerId);
        if (readerOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        return readerOpt.get().getBalance();
    }
    
    /**
     * 扣除读者余额
     * @param readerId 读者ID
     * @param amount 扣除金额
     * @return 是否成功
     */
    @Transactional
    public boolean deductBalance(Long readerId, BigDecimal amount) {
        Optional<Reader> readerOpt = readerRepository.findById(readerId);
        if (readerOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        Reader reader = readerOpt.get();
        if (reader.getBalance().compareTo(amount) < 0) {
            return false; // 余额不足
        }
        
        reader.setBalance(reader.getBalance().subtract(amount));
        readerRepository.save(reader);
        return true;
    }
    
    /**
     * 增加读者余额
     * @param readerId 读者ID
     * @param amount 增加金额
     * @return 是否成功
     */
    @Transactional
    public boolean addBalance(Long readerId, BigDecimal amount) {
        Optional<Reader> readerOpt = readerRepository.findById(readerId);
        if (readerOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        Reader reader = readerOpt.get();
        reader.setBalance(reader.getBalance().add(amount));
        readerRepository.save(reader);
        return true;
    }
    
    /**
     * 根据用户名查找读者
     */
    public Optional<Reader> findByReaderName(String readerName) {
        return readerRepository.findByReaderName(readerName);
    }
    
    /**
     * 根据ID查找读者
     */
    public Optional<Reader> findById(Long readerId) {
        return readerRepository.findById(readerId);
    }
    
    /**
     * 创建新读者
     * @param reader 读者信息
     * @return 创建的读者
     */
    @Transactional
    public Reader createReader(Reader reader) {
        // 检查读者名是否已存在
        if (readerRepository.existsByReaderName(reader.getReaderName())) {
            throw new RuntimeException("读者名已存在");
        }
        
        // 如果提供了密码，则加密
        if (reader.getPassword() != null && !reader.getPassword().isEmpty()) {
            String hashedPassword = PasswordHasher.hashPassword(reader.getPassword());
            reader.setPassword(hashedPassword);
        }
        
        // 设置默认值
        if (reader.getBalance() == null) {
            reader.setBalance(BigDecimal.ZERO);
        }
        if (reader.getIsCollectVisible() == null) {
            reader.setIsCollectVisible("是");
        }
        if (reader.getIsRecommendVisible() == null) {
            reader.setIsRecommendVisible("是");
        }
        
        return readerRepository.save(reader);
    }
    
    /**
     * 更新读者信息
     * @param readerId 读者ID
     * @param reader 更新的读者信息
     * @return 更新后的读者
     */
    @Transactional
    public Reader updateReader(Long readerId, Reader reader) {
        // 检查读者是否存在
        Optional<Reader> existingReaderOpt = readerRepository.findById(readerId);
        if (existingReaderOpt.isEmpty()) {
            throw new RuntimeException("读者不存在");
        }
        
        Reader existingReader = existingReaderOpt.get();
        
        // 如果更新读者名，检查是否与其他读者重复
        if (reader.getReaderName() != null && !reader.getReaderName().equals(existingReader.getReaderName())) {
            if (readerRepository.existsByReaderName(reader.getReaderName())) {
                throw new RuntimeException("读者名已存在");
            }
            existingReader.setReaderName(reader.getReaderName());
        }
        
        // 更新其他字段
        if (reader.getPhone() != null) {
            existingReader.setPhone(reader.getPhone());
        }
        if (reader.getGender() != null) {
            existingReader.setGender(reader.getGender());
        }
        if (reader.getAvatarUrl() != null) {
            existingReader.setAvatarUrl(reader.getAvatarUrl());
        }
        if (reader.getBackgroundUrl() != null) {
            existingReader.setBackgroundUrl(reader.getBackgroundUrl());
        }
        if (reader.getIsCollectVisible() != null) {
            existingReader.setIsCollectVisible(reader.getIsCollectVisible());
        }
        if (reader.getIsRecommendVisible() != null) {
            existingReader.setIsRecommendVisible(reader.getIsRecommendVisible());
        }
        if (reader.getBalance() != null) {
            existingReader.setBalance(reader.getBalance());
        }
        
        // 如果提供了新密码，则加密
        if (reader.getPassword() != null && !reader.getPassword().isEmpty()) {
            String hashedPassword = PasswordHasher.hashPassword(reader.getPassword());
            existingReader.setPassword(hashedPassword);
        }
        
        return readerRepository.save(existingReader);
    }
    
    /**
     * 删除读者
     * @param readerId 读者ID
     */
    @Transactional
    public void deleteReader(Long readerId) {
        // 检查读者是否存在
        if (!readerRepository.existsById(readerId)) {
            throw new RuntimeException("读者不存在");
        }
        
        readerRepository.deleteById(readerId);
    }
    
    /**
     * 上传读者头像
     * @param readerId 读者ID
     * @param file 头像文件
     * @return 头像URL
     */
    @Transactional
    public String uploadAvatar(Long readerId, MultipartFile file) {
        // 检查读者是否存在
        Optional<Reader> readerOpt = readerRepository.findById(readerId);
        if (readerOpt.isEmpty()) {
            throw new RuntimeException("读者不存在");
        }
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("头像文件不能为空");
        }
        
        // 上传文件并获取URL
        String avatarUrl = storageService.uploadFile(file, "avatars");
        
        // 更新读者头像URL
        Reader reader = readerOpt.get();
        reader.setAvatarUrl(avatarUrl);
        readerRepository.save(reader);
        
        return avatarUrl;
    }
    
    /**
     * 上传读者背景
     * @param readerId 读者ID
     * @param file 背景文件
     * @return 背景URL
     */
    @Transactional
    public String uploadBackground(Long readerId, MultipartFile file) {
        // 检查读者是否存在
        Optional<Reader> readerOpt = readerRepository.findById(readerId);
        if (readerOpt.isEmpty()) {
            throw new RuntimeException("读者不存在");
        }
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("背景文件不能为空");
        }
        
        // 上传文件并获取URL
        String backgroundUrl = storageService.uploadFile(file, "backgrounds");
        
        // 更新读者背景URL
        Reader reader = readerOpt.get();
        reader.setBackgroundUrl(backgroundUrl);
        readerRepository.save(reader);
        
        return backgroundUrl;
    }
    
    /**
     * 通过读者名模糊搜索读者
     * @param keyword 关键词
     * @return 匹配的读者列表
     */
    public List<Reader> searchReadersByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        return readerRepository.findByReaderNameContaining(keyword.trim());
    }
    
    /**
     * 获取读者总数
     * @return 读者总数
     */
    public Long getTotalReadersCount() {
        return readerRepository.countTotalReaders();
    }
}

