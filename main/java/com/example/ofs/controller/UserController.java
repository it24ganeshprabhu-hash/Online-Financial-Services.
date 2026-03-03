package com.example.ofs.controller;

import com.example.ofs.dto.LoginDto;
import com.example.ofs.dto.UpdateDto;
import com.example.ofs.model.mysql.History;
import com.example.ofs.model.mysql.Notification;
import com.example.ofs.model.mysql.Photo;
import com.example.ofs.model.mysql.User;
import com.example.ofs.model.mongodb.UserProfile;
import com.example.ofs.repository.mysql.HistoryRepo;
import com.example.ofs.repository.mysql.NotifiRepo;
import com.example.ofs.repository.mysql.photoRepo;
import com.example.ofs.repository.mysql.UserRepo;
import com.example.ofs.repository.mongodb.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    HistoryRepo historyRepo;

    @Autowired
    NotifiRepo notifiRepo;

    @Autowired
    photoRepo photoRepo;

    @Autowired
    UserProfileRepository aadhaarRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        Optional<UserProfile> aadhaarOpt = aadhaarRepo.findByAadhaarNumber(user.getAadhaarNumber());

        if (aadhaarOpt.isEmpty()) {
            return "Registration Failed: Aadhaar number not found in Government database.";
        }

        if (!aadhaarOpt.get().isVerified()) {
            return "Verification Required: Please verify your Aadhaar at the nearest center first.";
        }

        if ("CURRENT".equalsIgnoreCase(user.getAccountType())) {
            user.setOverdraftLimit(50000.0);
        } else if ("SALARY".equalsIgnoreCase(user.getAccountType())) {
            user.setOverdraftLimit(5000.0);
        } else {
            user.setOverdraftLimit(0.0);
        }

        History h1 = new History();
        h1.setDescription("New " + user.getAccountType() + " account created for: " + user.getUsername());
        historyRepo.save(h1);

        if ("admin".equalsIgnoreCase(user.getRole())) {
            return "Admin authorization required";
        }

        userRepo.save(user);
        return "Signup Successful for " + user.getAccountType() + " account.";
    }


    @Transactional
    @PostMapping("/add/{adminId}")
    public String adminCreateUser(@RequestBody User newUser, @PathVariable int adminId) {

        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!"admin".equalsIgnoreCase(admin.getRole())) {
            return "Unauthorized: Admin access required.";
        }


        if (userRepo.findByUsername(newUser.getUsername()) != null) {
            return "Failed: Username '" + newUser.getUsername() + "' is already taken. Please provide a different username for this account type.";
        }


        Optional<User> existingType = userRepo.findByAadhaarNumberAndAccountType(
                newUser.getAadhaarNumber(), newUser.getAccountType());
        if (existingType.isPresent()) {
            return "Failed: A " + newUser.getAccountType() + " account is already linked to this Aadhaar. Use a different account type.";
        }


        UserProfile govtRecord = aadhaarRepo.findByAadhaarNumber(newUser.getAadhaarNumber())
                .orElseThrow(() -> new RuntimeException("Aadhaar not found in Government Database."));
        if (!govtRecord.isVerified()) {
            return "Failed: Aadhaar is not verified in Government records.";
        }


        newUser.setRole("customer");
        if ("CURRENT".equalsIgnoreCase(newUser.getAccountType())) {
            newUser.setOverdraftLimit(50000.0);
        } else if ("SALARY".equalsIgnoreCase(newUser.getAccountType())) {
            newUser.setOverdraftLimit(5000.0);
        } else {
            newUser.setOverdraftLimit(0.0);
        }


        User savedUser = userRepo.save(newUser);

        History history = new History();
        history.setUserId(adminId);
        history.setDescription("Admin created " + newUser.getAccountType() + " account for " + newUser.getName() + " (Username: " + newUser.getUsername() + ")");
        historyRepo.save(history);

        Notification note = new Notification();
        note.setUserId(savedUser.getId());
        note.setTitle("New Account Activated");
        note.setDescription("Your " + savedUser.getAccountType() + " account is now ready to use.");
        note.setType(Notification.NotificationType.INFO);
        notifiRepo.save(note);

        return "Success: " + savedUser.getAccountType() + " account created for Aadhaar " + savedUser.getAadhaarNumber();
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto u) {
        User user = userRepo.findByUsername(u.getUsername());
        if (user == null) return "User Not Found";
        if (!u.getPassword().equals(user.getPassword())) return "Password Incorrect";
        if (!u.getRole().equals(user.getRole())) return "Role Incorrect";

        if ("admin".equalsIgnoreCase(user.getRole())) {
            Notification n1 = new Notification();
            n1.setUserId(user.getId());
            n1.setType(Notification.NotificationType.WARNING);
            n1.setDescription(user.getUsername() + " has logged in as admin");
            n1.setTitle("Admin logged in");
            notifiRepo.save(n1);
        }
        return String.valueOf(user.getId());
    }

    @GetMapping("/get-details/{id}")
    public User getDetails(@PathVariable int id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    @PostMapping("/update")
    public String value(@RequestBody UpdateDto obj) {
        User user = userRepo.findById(obj.getId()).orElseThrow(() -> new RuntimeException("Not Found"));
        Notification n1 = new Notification();
        n1.setUserId(user.getId());
        n1.setType(Notification.NotificationType.INFO);

        if (obj.getKey().equalsIgnoreCase("name")) {
            user.setName(obj.getValue());
            n1.setTitle("Changed name");
            n1.setDescription("Name changed to " + obj.getValue());
        } else if (obj.getKey().equalsIgnoreCase("password")) {
            user.setPassword(obj.getValue());
            n1.setTitle("Changed password");
            n1.setDescription("Security credentials updated.");
        } else if (obj.getKey().equalsIgnoreCase("email")) {
            user.setEmail(obj.getValue());
            n1.setTitle("Changed email");
            n1.setDescription("Email updated to " + obj.getValue());
        } else if (obj.getKey().equalsIgnoreCase("address")) {
            user.setAddress(obj.getValue());
            n1.setTitle("Changed address");
            n1.setDescription("Address updated.");
        } else if (obj.getKey().equalsIgnoreCase("dob")) {
            user.setDob(obj.getValue());
            n1.setTitle("Changed DOB");
            n1.setDescription("DOB updated.");
        } else {
            return "Invalid key";
        }

        notifiRepo.save(n1);
        userRepo.save(user);
        return "Update done successfully";
    }

    @PostMapping("/update-photo")
    public String updatePhoto(@RequestBody Map<String, Object> payload) {
        int userId = (int) payload.get("userId");
        String photoUrl = (String) payload.get("photoUrl");

        Photo photo = photoRepo.findByUserId(userId)
                .orElse(new Photo());

        photo.setUserId(userId);
        photo.setPhotoUrl(photoUrl);

        photoRepo.save(photo);
        return "Photo updated successfully";
    }

    @GetMapping("/get-profile-photo/{userId}")
    public Photo getPhoto(@PathVariable int userId) {
        return photoRepo.findByUserId(userId).orElse(null);
    }

    @GetMapping("/users")
    public List<User> getAllUser(@RequestParam String sortBy, @RequestParam String order) {
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return userRepo.findAllByRole("customer", sort);
    }

    @GetMapping("/users/{keyword}")
    public List<User> getUsers(@PathVariable String keyword) {
        return userRepo.findByUsernameContainingIgnoreCaseAndRole(keyword, "customer");
    }

    @DeleteMapping("/api/users/{id}/admin/{adminId}")
    public String deleteUser(@PathVariable("id") int targetUserId, @PathVariable("adminId") int actingAdminId) {

        User user = userRepo.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getBalance() > 0) {
            return "Cannot delete: Account still has a balance of ₹" + user.getBalance();
        }


        notifiRepo.deleteByUserId(targetUserId);


        photoRepo.findByUserId(targetUserId).ifPresent(p -> photoRepo.delete(p));


        History h1 = new History();
        h1.setDescription("User " + user.getUsername() + " deleted by admin ID: " + actingAdminId);
        h1.setUserId(actingAdminId);
        historyRepo.save(h1);


        userRepo.delete(user);

        return "User deleted successfully";
    }
    @GetMapping("/verify-aadhaar/{number}")
    public UserProfile verifyAadhaar(@PathVariable String number) {
        return aadhaarRepo.findByAadhaarNumber(number)
                .filter(UserProfile::isVerified)
                .orElseThrow(() -> new RuntimeException("Verified Aadhaar not found"));
    }
}