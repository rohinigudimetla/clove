package com.rohini.clove.cafe;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    int orderNumber;
    Instant orderedAt;
    String item;
    int quantity;
    int priceCents;
}
