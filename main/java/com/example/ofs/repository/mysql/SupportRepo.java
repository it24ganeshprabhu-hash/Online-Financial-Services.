package com.example.ofs.repository.mysql;

import com.example.ofs.model.mysql.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRepo extends JpaRepository<SupportTicket , Long> {

    List<SupportTicket> findAllByOrderByCreatedAtDesc();
}
