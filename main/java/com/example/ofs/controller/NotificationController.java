package com.example.ofs.controller;

import com.example.ofs.model.mysql.Notification;
import com.example.ofs.repository.mysql.NotifiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotifiRepo notifiRepo;


    @GetMapping("/notifications")
    public List<Notification> getnotifications(@RequestParam String role, @RequestParam(required = false) Integer userId) {
        if ("admin".equalsIgnoreCase(role)) {
            return notifiRepo.findAll();
        } else {

            return notifiRepo.findActiveNotifications(userId);
        }
    }


    @PutMapping("/notifications/{id}/read")
    public Notification readnotifications(@PathVariable int id) {
        return notifiRepo.findById(id).map(n -> {
            n.setUnread(false);
            return notifiRepo.save(n);
        }).orElse(null);
    }


    @PutMapping("/notifications/read-all")
    public String readallnotifications(@RequestParam String role, @RequestParam(required = false) Integer userId) {
        if ("admin".equalsIgnoreCase(role)) {

            List<Notification> all = notifiRepo.findAll();
            all.forEach(n -> n.setUnread(false));
            notifiRepo.saveAll(all);
            return all.size() + " system notifications marked as read";
        } else {
            if (userId == null) return "User ID is required for this operation";


            int count = notifiRepo.markAllAsReadByUserId(userId);
            return count + " notifications marked as read for user " + userId;
        }
    }
}