package com.javangers.citronix.web.vm.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreeUpdateRequestVM {

    @NotNull(message = "Planting date is required")
    private LocalDate plantingDate;

    @NotNull(message = "Field ID is required")
    private UUID fieldId;


}