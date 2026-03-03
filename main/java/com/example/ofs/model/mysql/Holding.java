package com.example.ofs.model.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "holdings")
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String symbol;
    private Integer quantity;
    private Double avgPrice;


    public Holding() {}


    public Holding(Long userId, String symbol, Integer quantity, Double avgPrice) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.avgPrice = avgPrice != null ? avgPrice : 0.0;
    }
}