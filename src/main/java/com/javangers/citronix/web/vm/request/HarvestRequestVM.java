package com.javangers.citronix.web.vm.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HarvestRequestVM {

    @NotNull(message = "Harvest Date is required")
    private LocalDate harvestDate;

    @NotNull(message = "Item (Field/Farm) is required")
    private UUID item;

}

