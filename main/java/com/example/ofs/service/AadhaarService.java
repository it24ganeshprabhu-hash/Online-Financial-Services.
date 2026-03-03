package com.example.ofs.service;



import com.example.ofs.model.mongodb.UserProfile;
import com.example.ofs.repository.mongodb.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AadhaarService {
    private final UserProfileRepository userProfileRepository;
    private final OtpService otpService;

    public AadhaarService(UserProfileRepository userProfileRepository, OtpService otpService) {
        this.userProfileRepository = userProfileRepository;
        this.otpService = otpService;
    }

    // user data by Aadhaar number
    public Optional<UserProfile> fetchUserByAadhaar(String aadhaarNumber) {
        return userProfileRepository.findByAadhaarNumber(aadhaarNumber);
    }

    //  sending OTP
    public String startVerification(String aadhaarNumber) {
        Optional<UserProfile> profileOpt = userProfileRepository.findByAadhaarNumber(aadhaarNumber);
        if (profileOpt.isEmpty()) return "User not found!";
        UserProfile profile = profileOpt.get();
        otpService.generateAndSendOtp(profile);
        return "OTP sent to " + profile.getGmail();
    }

    // Verify OTP
    public UserProfile completeVerification(String aadhaarNumber, String otp) {
        boolean verified = otpService.verifyOtp(aadhaarNumber, otp);
        if (verified) {
            return userProfileRepository.findByAadhaarNumber(aadhaarNumber).map(profile -> {
                profile.setVerified(true);
                return userProfileRepository.save(profile);
            }).orElse(null);
        }
        return null;
    }

    // Get verified user data
    public Optional<UserProfile> getVerifiedUserData(String aadhaarNumber) {
        return userProfileRepository.findByAadhaarNumber(aadhaarNumber)
                .filter(UserProfile::isVerified);
    }
}