package com.example.ofs.repository.mongodb;

import com.example.ofs.model.mongodb.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByAadhaarNumber(String aadhaarNumber);
}
