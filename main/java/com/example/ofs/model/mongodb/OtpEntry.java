package com.example.ofs.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Setter
@Getter
@Document(collection = "otp_records")
public class OtpEntry {

    @Id
    private String id;

    private String aadhaarNumber;
    private String otp;
    private java.time.Instant expiryTime;

}