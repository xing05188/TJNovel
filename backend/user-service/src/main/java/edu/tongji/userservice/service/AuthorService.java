package edu.tongji.userservice.service;

import edu.tongji.common.service.StorageService;
import edu.tongji.userservice.dto.LoginResponseDto;
import edu.tongji.userservice.entity.Author;
import edu.tongji.userservice.repository.AuthorRepository;
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
 * 作者服务类
 */
@Service
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private StorageService storageService;
    
    /**
     * 作者注册
     * @param authorName 作者名
     * @param password 密码
     * @param phone 手机号
     * @return 注册结果
     */
    @Transactional
    public Author register(String authorName, String password, String phone) {
        // 检查作者名是否已存在
        if (authorRepository.existsByAuthorName(authorName)) {
            throw new RuntimeException("作者名已存在");
        }
        
        // 加密密码
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        // 创建作者对象
        Author author = new Author();
        author.setAuthorName(authorName);
        author.setPassword(hashedPassword);
        author.setPhone(phone);
        author.setEarning(BigDecimal.ZERO);
        
        // 保存到数据库
        return authorRepository.save(author);
    }
    
    /**
     * 作者登录
     * @param authorName 作者名
     * @param password 密码
     * @return 登录响应（包含JWT Token）
     */
    public LoginResponseDto login(String authorName, String password) {
        // 查找作者
        Optional<Author> authorOpt = authorRepository.findByAuthorName(authorName);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("作者名不存在");
        }
        
        Author author = authorOpt.get();
        
        // 验证密码
        if (!PasswordHasher.verifyPassword(password, author.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(authorName, author.getAuthorId());
        
        // 返回登录响应
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setAuthorName(author.getAuthorName());
        response.setAuthorId(author.getAuthorId());
        return response;
    }
    
    /**
     * 重置密码
     * @param authorName 作者名
     * @param phone 手机号
     * @param newPassword 新密码
     * @return 重置结果
     */
    @Transactional
    public Author resetPassword(String authorName, String phone, String newPassword) {
        // 查找作者
        Optional<Author> authorOpt = authorRepository.findByAuthorName(authorName);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("作者不存在");
        }
        
        Author author = authorOpt.get();
        
        // 验证手机号
        if (!phone.equals(author.getPhone())) {
            throw new RuntimeException("手机号码不匹配");
        }
        
        // 加密新密码
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        
        // 更新密码
        author.setPassword(hashedPassword);
        
        // 保存到数据库
        return authorRepository.save(author);
    }
    
    /**
     * 修改密码
     * @param authorName 作者名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @Transactional
    public Author changePassword(String authorName, String oldPassword, String newPassword) {
        // 查找作者
        Optional<Author> authorOpt = authorRepository.findByAuthorName(authorName);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("作者不存在");
        }
        
        Author author = authorOpt.get();
        
        // 验证旧密码
        if (!PasswordHasher.verifyPassword(oldPassword, author.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        
        // 加密新密码
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        
        // 更新密码
        author.setPassword(hashedPassword);
        
        // 保存到数据库
        return authorRepository.save(author);
    }
    
    /**
     * 根据作者名查找作者
     */
    public Optional<Author> findByAuthorName(String authorName) {
        return authorRepository.findByAuthorName(authorName);
    }
    
    /**
     * 根据ID查找作者
     */
    public Optional<Author> findById(Long authorId) {
        return authorRepository.findById(authorId);
    }
    
    /**
     * 获取所有作者
     * @return 所有作者列表
     */
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }
    
    /**
     * 根据ID查找作者
     * @param authorId 作者ID
     * @return 作者信息
     */
    public Optional<Author> findAuthorById(Long authorId) {
        return authorRepository.findById(authorId);
    }
    
    /**
     * 创建新作者
     * @param author 作者信息
     * @return 创建的作者
     */
    @Transactional
    public Author createAuthor(Author author) {
        // 检查作者名是否已存在
        if (authorRepository.existsByAuthorName(author.getAuthorName())) {
            throw new RuntimeException("作者名已存在");
        }
        
        // 如果提供了密码，则加密
        if (author.getPassword() != null && !author.getPassword().isEmpty()) {
            String hashedPassword = PasswordHasher.hashPassword(author.getPassword());
            author.setPassword(hashedPassword);
        }
        
        // 设置默认收益为0
        if (author.getEarning() == null) {
            author.setEarning(BigDecimal.ZERO);
        }
        
        return authorRepository.save(author);
    }
    
    /**
     * 更新作者信息
     * @param authorId 作者ID
     * @param author 更新的作者信息
     * @return 更新后的作者
     */
    @Transactional
    public Author updateAuthor(Long authorId, Author author) {
        // 检查作者是否存在
        Optional<Author> existingAuthorOpt = authorRepository.findById(authorId);
        if (existingAuthorOpt.isEmpty()) {
            throw new RuntimeException("作者不存在");
        }
        
        Author existingAuthor = existingAuthorOpt.get();
        
        // 如果更新作者名，检查是否与其他作者重复
        if (author.getAuthorName() != null && !author.getAuthorName().equals(existingAuthor.getAuthorName())) {
            if (authorRepository.existsByAuthorName(author.getAuthorName())) {
                throw new RuntimeException("作者名已存在");
            }
            existingAuthor.setAuthorName(author.getAuthorName());
        }
        
        // 更新其他字段
        if (author.getPhone() != null) {
            existingAuthor.setPhone(author.getPhone());
        }
        if (author.getIntroduction() != null) {
            existingAuthor.setIntroduction(author.getIntroduction());
        }
        if (author.getAvatarUrl() != null) {
            existingAuthor.setAvatarUrl(author.getAvatarUrl());
        }
        if (author.getEarning() != null) {
            existingAuthor.setEarning(author.getEarning());
        }
        
        // 如果提供了新密码，则加密
        if (author.getPassword() != null && !author.getPassword().isEmpty()) {
            String hashedPassword = PasswordHasher.hashPassword(author.getPassword());
            existingAuthor.setPassword(hashedPassword);
        }
        
        return authorRepository.save(existingAuthor);
    }
    
    /**
     * 删除作者
     * @param authorId 作者ID
     */
    @Transactional
    public void deleteAuthor(Long authorId) {
        // 检查作者是否存在
        if (!authorRepository.existsById(authorId)) {
            throw new RuntimeException("作者不存在");
        }
        
        authorRepository.deleteById(authorId);
    }
    
    /**
     * 上传作者头像
     * @param authorId 作者ID
     * @param file 头像文件
     * @return 头像URL
     */
    @Transactional
    public String uploadAvatar(Long authorId, MultipartFile file) {
        // 检查作者是否存在
        Optional<Author> authorOpt = authorRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("作者不存在");
        }
        
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("头像文件不能为空");
        }
        
        // 上传文件并获取URL
        String avatarUrl = storageService.uploadFile(file, "avatars");
        
        // 更新作者头像URL
        Author author = authorOpt.get();
        author.setAvatarUrl(avatarUrl);
        authorRepository.save(author);
        
        return avatarUrl;
    }
    
    /**
     * 获取作者注册天数
     * @param authorId 作者ID
     * @return 注册天数
     */
    public long getAuthorRegisterDays(Long authorId) {
        // 检查作者是否存在
        Optional<Author> authorOpt = authorRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("作者不存在");
        }
        
        Author author = authorOpt.get();
        
        // 获取注册时间
        if (author.getRegisterTime() == null) {
            throw new RuntimeException("作者注册时间不存在");
        }
        
        // 计算注册天数
        java.time.LocalDateTime registerTime = author.getRegisterTime();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(registerTime, now);
        
        return duration.toDays();
    }
    
    /**
     * 通过作者名模糊搜索作者
     * @param keyword 关键词
     * @return 匹配的作者列表
     */
    public List<Author> searchAuthorsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        return authorRepository.findByAuthorNameContaining(keyword.trim());
    }
    
    /**
     * 获取作者总数
     * @return 作者总数
     */
    public Long getTotalAuthorsCount() {
        return authorRepository.countTotalAuthors();
    }
    
    /**
     * 增加作者收入
     * @param authorId 作者ID
     * @param amount 增加金额
     * @return 是否成功
     */
    @Transactional
    public boolean addEarning(Long authorId, BigDecimal amount) {
        Optional<Author> authorOpt = authorRepository.findById(authorId);
        if (authorOpt.isEmpty()) {
            throw new RuntimeException("作者不存在");
        }
        
        Author author = authorOpt.get();
        author.setEarning(author.getEarning().add(amount));
        authorRepository.save(author);
        return true;
    }
}
