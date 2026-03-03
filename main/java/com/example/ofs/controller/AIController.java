package com.example.ofs.controller;

import com.example.ofs.model.mysql.Transaction;
import com.example.ofs.model.mysql.User;
import com.example.ofs.service.SimpleAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private SimpleAiService aiService;

    @Autowired
    private com.example.ofs.repository.mysql.UserRepo userRepository;

    @Autowired
    private com.example.ofs.repository.mysql.TransactionRepo transactionRepository;

    @GetMapping("/consult")
    public String getConsultation(@RequestParam Integer userId, @RequestParam String message) {


        double balance = userRepository.findById(userId)
                .map(User::getBalance)
                .orElse(0.0);


        List<Transaction> transactions = transactionRepository.findTop5ByUserIdOrderByIdDesc(userId);


        List<String> history = transactions.stream()
                .map(t -> "Amount: ₹" + t.getAmount() + " on " + t.getDate())
                .collect(Collectors.toList());


        String context = String.format("User Balance: ₹%.2f. Recent History: %s",
                balance, String.join(" | ", history));

        return aiService.getFinancialAdvice(message, balance, context);
    }
}