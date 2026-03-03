package com.example.ofs.repository.mysql;

import com.example.ofs.model.mysql.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface photoRepo extends JpaRepository<Photo, Integer> {


    Optional<Photo> findByUserId(int userId);
}