package com.javangers.citronix.service;


import com.javangers.citronix.domain.Field;
import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.enumeration.Season;
import com.javangers.citronix.web.params.HarvestPageRequest;
import com.javangers.citronix.web.vm.request.HarvestRequestVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HarvestService {
    Harvest harvestField(LocalDate harvetsDate, UUID fields);
    Harvest harvestFarm(LocalDate harvetsDate, UUID fields);

    Harvest getHarvestById(UUID harvestId);
    Page<Harvest> getAllHarvests(Season season, Integer year, Pageable pageable);
    Harvest updateHarvest(UUID harvestId, HarvestRequestVM request);
    void deleteHarvest(UUID harvestId);
}
