package com.example.ofs.controller;

import com.example.ofs.model.mysql.User;
import com.example.ofs.repository.mysql.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepo userRepo;

    @PutMapping("/update-status/{targetId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable int targetId,
            @RequestParam String status,
            @RequestParam int adminId) {


        User admin = userRepo.findById(adminId).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(403).body("Unauthorized: Admin access required");
        }


        return userRepo.findById(targetId).map(user -> {
            user.setStatus(status.toUpperCase());
            userRepo.save(user);
            return ResponseEntity.ok("User status updated to " + status);
        }).orElse(ResponseEntity.notFound().build());
    }
}