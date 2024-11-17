package com.javangers.citronix.domain;

import com.javangers.citronix.domain.enumeration.Season;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "harvests")
public class Harvest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate harvestDate;

    @Enumerated(EnumType.STRING)
    private Season season;

    private Integer year;

    private Double totalQuantity;

    @ManyToOne
    private Farm farm;

    @OneToMany(mappedBy = "harvest")
    private List<HarvestDetail> harvestDetails;

    @OneToMany(mappedBy = "harvest")
    private List<Sales> sales;

}
