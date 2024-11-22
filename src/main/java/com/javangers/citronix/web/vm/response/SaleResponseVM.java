package com.javangers.citronix.web.vm.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseVM {
    private UUID id;
    private UUID harvestId;
    private Double quantity;
    private Double unitPrice;
    private String client;
    private LocalDate saleDate;
}