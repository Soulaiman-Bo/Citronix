package com.javangers.citronix.web.params;

import com.javangers.citronix.domain.enumeration.Season;
import lombok.Data;

@Data
public class HarvestPageRequest {
    private Season season;
    private Integer year;
    private int page = 0;
    private int size = 20;
}