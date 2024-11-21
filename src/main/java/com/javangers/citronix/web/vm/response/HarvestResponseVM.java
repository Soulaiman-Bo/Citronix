package com.javangers.citronix.web.vm.response;


import com.javangers.citronix.domain.enumeration.Season;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class HarvestResponseVM {
    private UUID id;
    private LocalDate harvestDate;
    private Double totalQuantity;
}
