package com.example.ofs.controller;

import com.example.ofs.model.mysql.History;
import com.example.ofs.repository.mysql.HistoryRepo;
import com.example.ofs.repository.mysql.UserRepo;
import com.example.ofs.model.mysql.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"*"})
public class HistoryController {

    @Autowired
    HistoryRepo historyRepo;

    @Autowired
    UserRepo userRepo;


    @GetMapping("/histories")
    public List<History> getAllHistories() {
        return historyRepo.findAll();
    }


    @GetMapping("/histories/admin/{adminId}")
    public List<History> getAdminHistory(@PathVariable Integer adminId) {

        User user = userRepo.findById(adminId).orElse(null);

        if (user != null && "admin".equalsIgnoreCase(user.getRole())) {

            return historyRepo.findAll();
        } else {

            return historyRepo.findAllByUserId(adminId);
        }
    }
}