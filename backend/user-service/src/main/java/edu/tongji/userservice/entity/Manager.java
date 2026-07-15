package edu.tongji.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MANAGER")
public class Manager {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MANAGER_ID")
    private Long managerId;
    
    @Column(name = "MANAGER_NAME", nullable = false, length = 20, unique = true)
    private String managerName;
    
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;
    
    // Getters and Setters
    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
