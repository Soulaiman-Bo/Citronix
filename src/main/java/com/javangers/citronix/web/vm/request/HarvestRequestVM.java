package com.javangers.citronix.web.vm.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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

    @NotNull(message = "Harvest date cannot be null")
    @PastOrPresent(message = "Harvest date must be in the past or present")
    private LocalDate harvestDate;

    @NotNull(message = "Entity ID is required")
    private UUID item;

}

