package com.example.ofs.model.mysql;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profile_photos")
@Data
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private int userId;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String photoUrl;


    public Photo() {}


    public Photo(int userId, String photoUrl) {
        this.userId = userId;
        this.photoUrl = photoUrl;
    }
}