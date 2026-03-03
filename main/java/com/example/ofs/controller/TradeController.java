package com.example.ofs.controller;

import com.example.ofs.model.mysql.Holding;
import com.example.ofs.model.mysql.Portfolio;
import com.example.ofs.repository.mysql.HoldingRepo;
import com.example.ofs.repository.mysql.PortfolioRepo;
import com.example.ofs.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
@CrossOrigin(origins = "*")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PortfolioRepo portfolioRepo;

    @Autowired
    private HoldingRepo holdingRepo;

    @PostMapping("/execute")
    public ResponseEntity<String> executeOrder(@RequestParam Integer userId, @RequestParam String symbol,
                                               @RequestParam String type, @RequestParam Integer qty, @RequestParam Double price) {
        String result = tradeService.processTrade(userId, symbol, type, qty, price);
        return result.contains("Error") ? ResponseEntity.badRequest().body(result) : ResponseEntity.ok(result);
    }

    @PostMapping("/transfer-funds")
    public ResponseEntity<String> transferFunds(@RequestParam Integer userId, @RequestParam Double amount) {
        String result = tradeService.transferFundsToPortfolio(userId, amount);
        return result.contains("Error") ? ResponseEntity.badRequest().body(result) : ResponseEntity.ok(result);
    }

    @PostMapping("/withdraw-funds")
    public ResponseEntity<String> withdrawFunds(@RequestParam Integer userId, @RequestParam Double amount) {
        String result = tradeService.withdrawFundsFromPortfolio(userId, amount);
        return result.contains("Error") ? ResponseEntity.badRequest().body(result) : ResponseEntity.ok(result);
    }

    @GetMapping("/portfolio-details")
    public ResponseEntity<Portfolio> getPortfolio(@RequestParam Integer userId) {
        Portfolio p = portfolioRepo.findByUserId(Long.valueOf(userId));
        return (p != null) ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @GetMapping("/holdings")
    public ResponseEntity<List<Holding>> getHoldings(@RequestParam Integer userId) {
        return ResponseEntity.ok(holdingRepo.findAllByUserId(Long.valueOf(userId)));
    }
}