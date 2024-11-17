package com.javangers.citronix.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate saleDate;
    private Double quantity;
    private Double unitPrice;
    private String client;

    @ManyToOne
    private Harvest harvest;
}
