package com.example.ofs.repository.mysql;

import com.example.ofs.model.mysql.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepo extends JpaRepository<Portfolio,Long> {
    Portfolio findByUserId(Long userId);
}
