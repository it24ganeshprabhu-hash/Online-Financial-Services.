package com.example.ofs.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "user_profiles")
public class UserProfile {

    @Id
    private String id;

    private String aadhaarNumber;
    private String name;
    private String address;
    private String dob;
    private String gmail;

    private boolean verified = false;

}