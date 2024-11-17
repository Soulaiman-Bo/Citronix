package com.javangers.citronix.web.vm.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldRequestVM {
    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.1", message = "Area must be at least 0.1 hectares")
    private Double area;

    @NotNull(message = "Farm ID is required")
    private UUID farmId;

}