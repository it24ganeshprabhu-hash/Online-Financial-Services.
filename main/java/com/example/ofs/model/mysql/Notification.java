package com.example.ofs.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime date;


    @Column(nullable = true)
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(nullable = false)
    private boolean unread = true;

    public enum NotificationType {
        INFO, WARNING, CRITICAL
    }
}