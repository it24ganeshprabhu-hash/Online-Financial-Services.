package com.example.ofs.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role; // "user" or "admin"

    @Column(nullable = false)
    private double balance;


    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private String dob;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 12)
    private String aadhaarNumber;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private double overdraftLimit = 0.0;
}