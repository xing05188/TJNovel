package edu.tongji.userservice.service;

import edu.tongji.userservice.dto.LoginResponseDto;
import edu.tongji.userservice.entity.Manager;
import edu.tongji.userservice.repository.ManagerRepository;
import edu.tongji.userservice.util.JwtUtil;
import edu.tongji.userservice.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 管理员服务类
 */
@Service
public class ManagerService {
    
    @Autowired
    private ManagerRepository managerRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 管理员注册
     * @param managerName 管理员名
     * @param password 密码
     * @return 注册结果
     */
    @Transactional
    public Manager register(String managerName, String password) {
        // 检查管理员名是否已存在
        if (managerRepository.existsByManagerName(managerName)) {
            throw new RuntimeException("管理员名已存在");
        }
        
        // 加密密码
        String hashedPassword = PasswordHasher.hashPassword(password);
        
        // 创建管理员对象
        Manager manager = new Manager();
        manager.setManagerName(managerName);
        manager.setPassword(hashedPassword);
        
        // 保存到数据库
        return managerRepository.save(manager);
    }
    
    /**
     * 重置管理员密码
     * @param managerName 管理员名
     * @param newPassword 新密码
     * @return 重置结果
     */
    @Transactional
    public Manager resetPassword(String managerName, String newPassword) {
        // 查找管理员
        Optional<Manager> managerOpt = managerRepository.findByManagerName(managerName);
        if (managerOpt.isEmpty()) {
            throw new RuntimeException("管理员不存在");
        }
        
        Manager manager = managerOpt.get();
        
        // 加密新密码
        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        
        // 更新密码
        manager.setPassword(hashedPassword);
        
        // 保存到数据库
        return managerRepository.save(manager);
    }
    
    /**
     * 管理员登录
     * @param managerName 管理员名
     * @param password 密码
     * @return 登录响应（包含JWT Token）
     */
    public LoginResponseDto login(String managerName, String password) {
        // 查找管理员
        Optional<Manager> managerOpt = managerRepository.findByManagerName(managerName);
        if (managerOpt.isEmpty()) {
            throw new RuntimeException("管理员名不存在");
        }
        
        Manager manager = managerOpt.get();
        
        // 验证密码
        if (!PasswordHasher.verifyPassword(password, manager.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(managerName, manager.getManagerId());
        
        // 返回登录响应
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setManagerName(manager.getManagerName());
        response.setManagerId(manager.getManagerId());
        return response;
    }
    
    /**
     * 根据管理员名查找管理员
     */
    public Optional<Manager> findByManagerName(String managerName) {
        return managerRepository.findByManagerName(managerName);
    }
    
    /**
     * 根据ID查找管理员
     */
    public Optional<Manager> findById(Long managerId) {
        return managerRepository.findById(managerId);
    }
    
    /**
     * 获取所有管理员
     * @return 所有管理员列表
     */
    public java.util.List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }
    
    /**
     * 根据ID查找管理员（返回实体）
     * @param managerId 管理员ID
     * @return 管理员实体
     */
    public Manager findManagerById(Long managerId) {
        Optional<Manager> managerOpt = managerRepository.findById(managerId);
        if (managerOpt.isEmpty()) {
            throw new RuntimeException("管理员不存在");
        }
        return managerOpt.get();
    }
    
    /**
     * 创建新管理员
     * @param manager 管理员信息
     * @return 创建的管理员
     */
    @Transactional
    public Manager createManager(Manager manager) {
        // 检查管理员名是否已存在
        if (managerRepository.existsByManagerName(manager.getManagerName())) {
            throw new RuntimeException("管理员名已存在");
        }
        
        // 如果提供了密码，则加密
        if (manager.getPassword() != null && !manager.getPassword().isEmpty()) {
            String hashedPassword = PasswordHasher.hashPassword(manager.getPassword());
            manager.setPassword(hashedPassword);
        }
        
        return managerRepository.save(manager);
    }
    
    /**
     * 更新管理员信息
     * @param managerId 管理员ID
     * @param manager 更新的管理员信息
     * @return 更新后的管理员
     */
    @Transactional
    public Manager updateManager(Long managerId, Manager manager) {
        // 检查管理员是否存在
        Optional<Manager> existingManagerOpt = managerRepository.findById(managerId);
        if (existingManagerOpt.isEmpty()) {
            throw new RuntimeException("管理员不存在");
        }
        
        Manager existingManager = existingManagerOpt.get();
        
        // 如果更新管理员名，检查是否与其他管理员重复
        if (manager.getManagerName() != null && !manager.getManagerName().equals(existingManager.getManagerName())) {
            if (managerRepository.existsByManagerName(manager.getManagerName())) {
                throw new RuntimeException("管理员名已存在");
            }
            existingManager.setManagerName(manager.getManagerName());
        }
        
        // 如果提供了新密码，则加密
        if (manager.getPassword() != null && !manager.getPassword().isEmpty()) {
            String hashedPassword = PasswordHasher.hashPassword(manager.getPassword());
            existingManager.setPassword(hashedPassword);
        }
        
        return managerRepository.save(existingManager);
    }
    
    /**
     * 删除管理员
     * @param managerId 管理员ID
     */
    @Transactional
    public void deleteManager(Long managerId) {
        // 检查管理员是否存在
        if (!managerRepository.existsById(managerId)) {
            throw new RuntimeException("管理员不存在");
        }
        
        managerRepository.deleteById(managerId);
    }
}
