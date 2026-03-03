package com.example.ofs.repository.mysql;

import com.example.ofs.model.mysql.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepo extends JpaRepository<History,Integer> {
    List<History> findAllByUserId(Integer userId);
}
