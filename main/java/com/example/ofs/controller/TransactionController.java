package com.example.ofs.controller;

import com.example.ofs.dto.TransactionDto;
import com.example.ofs.dto.TransferDto;
import com.example.ofs.model.mysql.Notification;
import com.example.ofs.model.mysql.Transaction;
import com.example.ofs.model.mysql.User;
import com.example.ofs.repository.mysql.NotifiRepo;
import com.example.ofs.repository.mysql.TransactionRepo;
import com.example.ofs.repository.mysql.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    NotifiRepo notifiRepo;

    @PostMapping("/deposit")
    @Transactional
    public String deposit(@RequestBody TransactionDto obj) {
        User user = userRepo.findById(obj.getId()).orElseThrow(() -> new RuntimeException("WrongId"));


        if ("FROZEN".equalsIgnoreCase(user.getStatus())) {
            return "Transaction Denied: Your account is currently FROZEN. Please contact support.";
        }

        double newBalance = user.getBalance() + obj.getAmount();
        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs " + obj.getAmount() + " Deposit Successful");
        t.setUserId(obj.getId());
        t.setDate(LocalDateTime.now());
        transactionRepo.save(t);

        return "Deposit Successful";
    }

    @PostMapping("/withdraw")
    @Transactional
    public String withdraw(@RequestBody TransactionDto obj) {
        User user = userRepo.findById(obj.getId()).orElseThrow(() -> new RuntimeException("WrongId"));


        if ("FROZEN".equalsIgnoreCase(user.getStatus())) {
            return "Transaction Denied: Your account is currently FROZEN.";
        }

        double newBalance = user.getBalance() - obj.getAmount();

        if (newBalance < -user.getOverdraftLimit()) {
            return "Transaction Denied: Overdraft Limit Reached";
        }

        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs " + obj.getAmount() + " Withdrawal (Balance: " + newBalance + ")");
        t.setUserId(obj.getId());
        t.setDate(LocalDateTime.now());
        transactionRepo.save(t);

        if (newBalance <= 0) {
            Notification notification = new Notification();
            notification.setTitle(newBalance == 0 ? "Account Balance Depleted" : "Overdraft Facility Used");
            notification.setDescription("User " + user.getUsername() + " balance: " + newBalance);
            notification.setType(Notification.NotificationType.CRITICAL);
            notification.setUserId(user.getId());
            notifiRepo.save(notification);
        }

        return "Withdrawal Successful";
    }

    @PostMapping("/transfer")
    @Transactional
    public String transfer(@RequestBody TransferDto obj) {
        User sender = userRepo.findById(obj.getId())
                .orElseThrow(() -> new RuntimeException("Not Found"));
        User rec = userRepo.findByUsername(obj.getUsername());


        if ("FROZEN".equalsIgnoreCase(sender.getStatus())) {
            return "Transaction Denied: Your account is currently FROZEN.";
        }

        if (rec == null) { return "Receiver not found"; }
        if (sender.getId() == rec.getId()) { return "Self transaction not allowed"; }
        if (obj.getAmount() < 1) { return "Invalid amount"; }
        if ("admin".equalsIgnoreCase(rec.getRole())) return "Cannot transfer amount to admin";

        double sbalance = sender.getBalance() - obj.getAmount();

        if (sbalance < -sender.getOverdraftLimit()) {
            return "Insufficient funds (Overdraft limit reached)";
        }

        double rbalance = rec.getBalance() + obj.getAmount();

        sender.setBalance(sbalance);
        rec.setBalance(rbalance);

        userRepo.save(sender);
        userRepo.save(rec);

        LocalDateTime transferTime = LocalDateTime.now();

        Transaction t1 = new Transaction();
        t1.setAmount(obj.getAmount());
        t1.setCurrBalance(sbalance);
        t1.setDescription("Rs " + obj.getAmount() + " Sent to user " + obj.getUsername());
        t1.setUserId(sender.getId());
        t1.setDate(transferTime);

        Transaction t2 = new Transaction();
        t2.setAmount(obj.getAmount());
        t2.setCurrBalance(rbalance);
        t2.setDescription("Rs " + obj.getAmount() + " Received from user " + sender.getUsername());
        t2.setUserId(rec.getId());
        t2.setDate(transferTime);

        transactionRepo.save(t1);
        transactionRepo.save(t2);

        if (sbalance <= 0) {
            Notification notification = new Notification();
            notification.setTitle("Whole Account transfer");
            notification.setDescription("User: " + sender.getUsername() + " transferred amount: " + obj.getAmount() + " to " + rec.getUsername());
            notification.setType(Notification.NotificationType.CRITICAL);
            notification.setUserId(sender.getId());
            notifiRepo.save(notification);
        }

        return "Transfer Done successfully";
    }

    @PostMapping("/apply-interest/{id}")
    @Transactional
    public String applyMonthlyInterest(@PathVariable int id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));


        if ("FROZEN".equalsIgnoreCase(user.getStatus())) {
            return "Interest Application Denied: Account is FROZEN.";
        }

        if (user.getBalance() <= 0) {
            return "No interest applied (Zero or Negative Balance)";
        }

        double monthlyRate = 0.05 / 12;
        double interestAmount = user.getBalance() * monthlyRate;

        interestAmount = Math.round(interestAmount * 100.0) / 100.0;

        if (interestAmount < 0.01) {
            return "Interest too low to credit";
        }

        double newBalance = user.getBalance() + interestAmount;
        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t = new Transaction();
        t.setAmount(interestAmount);
        t.setCurrBalance(newBalance);
        t.setDescription("Monthly Interest Credited (5% p.a.)");
        t.setUserId(id);
        t.setDate(LocalDateTime.now());
        transactionRepo.save(t);

        return "Interest of Rs " + interestAmount + " applied successfully";
    }

    @GetMapping("/passbook/{id}")
    public List<Transaction> getPassbook(@PathVariable int id) {
        return transactionRepo.findAllByUserId(id);
    }

    @PostMapping("/trading/deposit-log")
    @Transactional
    public String logTradingDeposit(@RequestBody TransactionDto obj) {
        User user = userRepo.findById(obj.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        if ("FROZEN".equalsIgnoreCase(user.getStatus())) {
            return "Logging Denied: Account Frozen";
        }

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(user.getBalance());
        t.setDescription("Transfer to Trading Portfolio: -Rs " + obj.getAmount());
        t.setUserId(obj.getId());
        t.setDate(LocalDateTime.now());
        transactionRepo.save(t);

        return "Trading Deposit Logged";
    }

    @PostMapping("/trading/withdraw-log")
    @Transactional
    public String logTradingWithdrawal(@RequestBody TransactionDto obj) {
        User user = userRepo.findById(obj.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        if ("FROZEN".equalsIgnoreCase(user.getStatus())) {
            return "Logging Denied: Account Frozen";
        }

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(user.getBalance());
        t.setDescription("Withdrawal from Trading Portfolio: +Rs " + obj.getAmount());
        t.setUserId(obj.getId());
        t.setDate(LocalDateTime.now());
        transactionRepo.save(t);

        return "Trading Withdrawal Logged";
    }
}