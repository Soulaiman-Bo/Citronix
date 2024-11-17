package com.javangers.citronix.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "harvest_details")
public class HarvestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double quantity;

    @ManyToOne
    private Harvest harvest;

    @ManyToOne
    private Tree tree;


}
