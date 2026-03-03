package com.example.ofs.service;

import com.example.ofs.model.mysql.Holding;
import com.example.ofs.model.mysql.Portfolio;
import com.example.ofs.model.mysql.User;
import com.example.ofs.model.mysql.Transaction; // Link to your main bank model
import com.example.ofs.repository.mysql.HoldingRepo;
import com.example.ofs.repository.mysql.PortfolioRepo;
import com.example.ofs.repository.mysql.UserRepo;
import com.example.ofs.repository.mysql.TransactionRepo; // Link to your main bank repo
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TradeService {

    @Autowired
    private PortfolioRepo portfolioRepo;

    @Autowired
    private HoldingRepo holdingRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Transactional
    public String transferFundsToPortfolio(Integer userId, Double amount) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getBalance() < amount) {
            return "Error: Insufficient Bank balance.";
        }

        Long longUserId = userId.longValue();
        Portfolio portfolio = portfolioRepo.findByUserId(longUserId);
        if (portfolio == null) {
            portfolio = new Portfolio();
            portfolio.setUserId(longUserId);
            portfolio.setCashBalance(0.0);
        }


        user.setBalance(user.getBalance() - amount);
        portfolio.setCashBalance((portfolio.getCashBalance() != null ? portfolio.getCashBalance() : 0.0) + amount);

        userRepo.save(user);
        portfolioRepo.save(portfolio);


        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setAmount(amount);
        t.setCurrBalance(user.getBalance());
        t.setDescription("Transfer to Trading Portfolio: -Rs " + amount);
        t.setDate(LocalDateTime.now());
        transactionRepo.save(t);

        return "Success: ₹" + amount + " added to Portfolio.";
    }

    @Transactional
    public String withdrawFundsFromPortfolio(Integer userId, Double amount) {
        Long longUserId = userId.longValue();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Portfolio portfolio = portfolioRepo.findByUserId(longUserId);
        if (portfolio == null || portfolio.getCashBalance() < amount) {
            return "Error: Insufficient Portfolio balance.";
        }


        portfolio.setCashBalance(portfolio.getCashBalance() - amount);
        user.setBalance(user.getBalance() + amount);

        userRepo.save(user);
        portfolioRepo.save(portfolio);


        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setAmount(amount);
        t.setCurrBalance(user.getBalance());
        t.setDescription("Withdrawal from Trading Portfolio: +Rs " + amount);
        t.setDate(LocalDateTime.now());
        transactionRepo.save(t);

        return "Success: ₹" + amount + " moved to Bank Account.";
    }

    @Transactional
    public String processTrade(Integer userId, String symbol, String type, Integer qty, Double price) {
        Long longUserId = userId.longValue();
        Portfolio portfolio = portfolioRepo.findByUserId(longUserId);
        if (portfolio == null) return "Error: Portfolio not found";

        double balance = (portfolio.getCashBalance() != null) ? portfolio.getCashBalance() : 0.0;
        double totalCost = (qty != null && price != null) ? (qty * price) : 0.0;

        if ("BUY".equalsIgnoreCase(type)) {
            if (balance < totalCost) return "Error: Insufficient Funds!";
            portfolio.setCashBalance(balance - totalCost);

            Holding holding = holdingRepo.findByUserIdAndSymbol(longUserId, symbol)
                    .orElse(new Holding(longUserId, symbol, 0, 0.0));

            double existingQty = holding.getQuantity();
            double existingAvgPrice = (holding.getAvgPrice() != null) ? holding.getAvgPrice() : 0.0;
            double totalQty = existingQty + qty;
            double newAvgPrice = ((existingQty * existingAvgPrice) + (qty * price)) / totalQty;

            holding.setQuantity((int) totalQty);
            holding.setAvgPrice(newAvgPrice);
            holdingRepo.save(holding);

        } else if ("SELL".equalsIgnoreCase(type)) {
            Holding holding = holdingRepo.findByUserIdAndSymbol(longUserId, symbol).orElse(null);
            if (holding == null || holding.getQuantity() < qty) return "Error: Not enough shares!";

            portfolio.setCashBalance(balance + totalCost);
            holding.setQuantity(holding.getQuantity() - qty);

            if (holding.getQuantity() <= 0) holdingRepo.delete(holding);
            else holdingRepo.save(holding);
        }

        portfolioRepo.save(portfolio);
        return "Success: Order Executed at ₹" + price;
    }
}