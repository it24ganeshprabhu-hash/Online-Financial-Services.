package com.example.ofs.controller;

import com.example.ofs.model.mysql.Notification;
import com.example.ofs.repository.mysql.NotifiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private NotifiRepo notifiRepo;

    @PostMapping("/send")
    public String sendBroadcast(@RequestBody Map<String, Object> payload) {
        try {
            Notification n = new Notification();


            int targetId = Integer.parseInt(payload.get("targetId").toString());

            n.setUserId(targetId);
            n.setTitle(payload.get("title").toString());
            n.setDescription(payload.get("message").toString());
            n.setUnread(true);


            n.setType(Notification.NotificationType.WARNING);

            notifiRepo.save(n);
            return "Alert dispatched successfully to target: " + targetId;
        } catch (Exception e) {
            return "Error sending alert: " + e.getMessage();
        }
    }
}