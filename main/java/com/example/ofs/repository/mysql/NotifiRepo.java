package com.example.ofs.repository.mysql;

import com.example.ofs.model.mysql.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotifiRepo extends JpaRepository<Notification, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.unread = false WHERE n.userId = :userId OR n.userId = 0")
    int markAllAsReadByUserId(@Param("userId") int userId);


    @Query("SELECT n FROM Notification n WHERE n.userId = :userId OR n.userId = 0 ORDER BY n.date DESC")
    List<Notification> findActiveNotifications(@Param("userId") Integer userId);

    List<Notification> findByUserId(Integer userId);

    void deleteByUserId(int targetUserId);
}