package com.example.ofs.controller;

import com.example.ofs.dto.TransferDto;
import com.example.ofs.service.QRCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QrController {

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private TransactionController transactionController;


    @GetMapping("/generate-my-qr/{username}")
    public ResponseEntity<Map<String, String>> getMyQR(@PathVariable String username) {
        Map<String, String> response = new HashMap<>();
        try {
            String qrBase64 = qrCodeService.generateQRCode(username);
            if (qrBase64 != null) {
                response.put("qrCode", qrBase64);
                return ResponseEntity.ok(response);
            }
            response.put("error", "QR Generation failed");
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/scan-pay")
    public ResponseEntity<Map<String, String>> processScanPayment(@RequestBody TransferDto transferDto) {
        Map<String, String> response = new HashMap<>();
        try {

            String result = transactionController.transfer(transferDto);

            if ("Transfer Done successfully".equalsIgnoreCase(result)) {
                response.put("status", "success");
                response.put("message", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", result);
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}