package com.example.ofs.repository.mysql;

import com.example.ofs.model.mysql.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Integer> {

    List<Transaction> findAllByUserId(int id);


    List<Transaction> findTop5ByUserIdOrderByIdDesc(Integer userId);
}