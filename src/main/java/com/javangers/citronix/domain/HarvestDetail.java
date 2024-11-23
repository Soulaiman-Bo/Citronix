package com.javangers.citronix.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
