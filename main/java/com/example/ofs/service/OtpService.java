package com.example.ofs.service;


import com.example.ofs.model.mongodb.OtpEntry;
import com.example.ofs.model.mongodb.UserProfile;
import com.example.ofs.repository.mongodb.OtpRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
    private final Random random = new Random();

    public OtpService(OtpRepository otpRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    public void generateAndSendOtp(UserProfile profile) {

        otpRepository.findByAadhaarNumber(profile.getAadhaarNumber())
                .ifPresent(otpRepository::delete);


        String otp = String.format("%06d", random.nextInt(999999));


        Instant expiry = Instant.now().plus(5, ChronoUnit.MINUTES);

        OtpEntry entry = new OtpEntry();
        entry.setAadhaarNumber(profile.getAadhaarNumber());
        entry.setOtp(otp);
        entry.setExpiryTime(expiry);
        otpRepository.save(entry);


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(profile.getGmail());
        message.setSubject("Your OTP for Aadhaar Verification");
        message.setText("Dear " + profile.getName() + ",\n\n" +
                "Your OTP is: " + otp + "\n" +
                "It will expire in 5 minutes.\n\n" +
                "If you did not request this, please ignore this email.");
        mailSender.send(message);

        System.out.println("OTP sent successfully to: " + profile.getGmail());
    }

    public boolean verifyOtp(String aadhaarNumber, String enteredOtp) {
        Optional<OtpEntry> entryOpt = otpRepository.findByAadhaarNumber(aadhaarNumber);

        if (entryOpt.isEmpty()) {
            System.out.println("Verification Failed: No OTP found for Aadhaar " + aadhaarNumber);
            return false;
        }

        OtpEntry entry = entryOpt.get();


        if (Instant.now().isAfter(entry.getExpiryTime())) {
            System.out.println("Verification Failed: OTP has expired at " + entry.getExpiryTime());
            otpRepository.delete(entry); // Clean up expired record
            return false;
        }

        boolean isValid = enteredOtp.equals(entry.getOtp());
        if (isValid) {
            otpRepository.delete(entry);
        }

        return isValid;
    }
}