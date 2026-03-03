package com.example.ofs.repository.mysql;

import com.example.ofs.model.mysql.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Use java.util.Optional, NOT OptionalDouble

@Repository
public interface HoldingRepo extends JpaRepository<Holding, Long> {


    Optional<Holding> findByUserIdAndSymbol(Long userId, String symbol);

    List<Holding> findAllByUserId(long l);
}