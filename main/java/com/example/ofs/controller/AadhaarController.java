package com.example.ofs.controller;

import com.example.ofs.model.mongodb.UserProfile;
import com.example.ofs.service.AadhaarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/aadhaar")
public class AadhaarController {

    private final AadhaarService aadhaarService;

    public AadhaarController(AadhaarService aadhaarService) {
        this.aadhaarService = aadhaarService;
    }


    @PostMapping("/start")
    public ResponseEntity<String> startVerification(@RequestParam String aadhaarNumber) {
        try {
            String result = aadhaarService.startVerification(aadhaarNumber);

            if (result.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error: " + e.getMessage());
        }
    }


    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestParam String aadhaarNumber,
                                       @RequestParam String otp) {
        UserProfile profile = aadhaarService.completeVerification(aadhaarNumber, otp);

        if (profile != null) {
            return ResponseEntity.ok(profile);
        } else {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid OTP or Session Expired");
        }
    }


    @GetMapping("/fetchUser")
    public ResponseEntity<UserProfile> fetchUserByAadhaar(@RequestParam String aadhaarNumber) {
        return aadhaarService.fetchUserByAadhaar(aadhaarNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/userData")
    public ResponseEntity<UserProfile> getUserData(@RequestParam String aadhaarNumber) {
        return aadhaarService.getVerifiedUserData(aadhaarNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}