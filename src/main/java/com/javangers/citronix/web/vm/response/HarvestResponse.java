package com.javangers.citronix.web.vm.response;

import com.javangers.citronix.domain.enumeration.Season;
import com.javangers.citronix.web.vm.request.HarvestDetailResponse;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class HarvestResponse {
    private UUID id;
    private LocalDate harvestDate;
    private Season season;
    private Double totalQuantity;
    private List<HarvestDetailResponse> harvestDetails;
}
