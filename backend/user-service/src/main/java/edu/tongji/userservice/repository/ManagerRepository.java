package edu.tongji.userservice.repository;

import edu.tongji.userservice.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByManagerName(String managerName);
    boolean existsByManagerName(String managerName);
}
