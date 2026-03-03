package com.example.ofs.repository.mongodb;



import com.example.ofs.model.mongodb.OtpEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpRepository extends MongoRepository<OtpEntry, String> {
    Optional<OtpEntry> findByAadhaarNumber(String aadhaarNumber);
}