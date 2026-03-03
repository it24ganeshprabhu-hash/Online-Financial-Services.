package com.example.ofs.controller;

import com.example.ofs.model.mysql.Notification;
import com.example.ofs.model.mysql.SupportTicket;
import com.example.ofs.model.mysql.User;
import com.example.ofs.repository.mysql.NotifiRepo;
import com.example.ofs.repository.mysql.SupportRepo;
import com.example.ofs.repository.mysql.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/support")
public class SupportController {

    @Autowired private SupportRepo supportRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private NotifiRepo notifiRepo;

    @PostMapping("/create")
    public SupportTicket createTicket(@RequestBody SupportTicket ticket) {

        User user = userRepo.findById(ticket.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ticket.setUserName(user.getName());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus("OPEN");
        return supportRepo.save(ticket);
    }


    @GetMapping("/admin/all")
    public List<SupportTicket> getAllTickets() {
        return supportRepo.findAllByOrderByCreatedAtDesc();
    }


    @PostMapping("/admin/reply/{ticketId}")
    public String replyToTicket(@PathVariable Long ticketId, @RequestBody Map<String, String> replyData) {
        SupportTicket ticket = supportRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String adminReply = replyData.get("message");


        ticket.setStatus("REPLIED");
        supportRepo.save(ticket);


        Notification n = new Notification();
        n.setUserId(ticket.getUserId());
        n.setTitle("Support Reply: " + ticket.getSubject());
        n.setDescription(adminReply);
        n.setType(Notification.NotificationType.INFO);
        n.setExpiryDate(LocalDateTime.now().plusDays(7));
        notifiRepo.save(n);

        return "Reply sent successfully";
    }
}