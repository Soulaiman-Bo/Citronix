package com.javangers.citronix.service;


import com.javangers.citronix.domain.Field;
import com.javangers.citronix.domain.Harvest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestService {
    Harvest harvestField(LocalDate harvetsDate, UUID fields);
    Harvest harvestFarm(LocalDate harvetsDate, UUID fields);

}
