package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.*;
import com.javangers.citronix.domain.enumeration.Season;
import com.javangers.citronix.repository.HarvestDetailRepository;
import com.javangers.citronix.repository.HarvestRepository;
import com.javangers.citronix.service.FarmService;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.service.HarvestService;
import com.javangers.citronix.service.SalesService;
import com.javangers.citronix.web.error.BusinessRuleViolationException;
import com.javangers.citronix.web.error.HarvestException;
import com.javangers.citronix.web.vm.request.HarvestRequestVM;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class HarvestServiceImpl implements HarvestService {
    private final FieldService fieldService;
    private final FarmService farmService;
    private final HarvestRepository harvestRepository;
    private final HarvestDetailRepository harvestDetailRepository;
    private SalesService salesService;

    @Autowired
    public void setSalesService(SalesService salesService) {
        this.salesService = salesService;
    }

    // Constructor for required dependencies
    public HarvestServiceImpl(FieldService fieldService,
                              FarmService farmService,
                              HarvestRepository harvestRepository,
                              HarvestDetailRepository harvestDetailRepository) {
        this.fieldService = fieldService;
        this.farmService = farmService;
        this.harvestRepository = harvestRepository;
        this.harvestDetailRepository = harvestDetailRepository;
    }

    @Override
    public Harvest harvestField(LocalDate harvestDate, UUID fieldId) {
        Field field = fieldService.getField(fieldId);

        if (harvestDate.getYear() != LocalDate.now().getYear()) {
            throw new BusinessRuleViolationException("Harvest Date should be current Year", "INVALID_HARVEST_YEAR");
        }

        Season season = determineSeason(harvestDate);

        validateUniqueSeasonHarvest(field, season);

        Harvest harvest = createInitialHarvest(harvestDate, season);
        List<HarvestDetail> harvestDetails = generateHarvestDetails(field, harvest, harvestDate, season);

        return finalizeHarvest(harvest, harvestDetails);
    }

    @Override
    public Harvest harvestFarm(LocalDate harvestDate, UUID farmId) {
        Farm farm = farmService.getFarm(farmId);
        List<Field> fields = farm.getFields();

        Season season = determineSeason(harvestDate);
        Harvest harvest = createInitialHarvest(harvestDate, season);
        List<HarvestDetail> harvestDetails = new ArrayList<>();


        for (Field field : fields) {
            validateUniqueSeasonHarvest(field, season);
            harvestDetails = generateHarvestDetails(field, harvest, harvestDate, season);

        }
        return finalizeHarvest(harvest, harvestDetails);
    }

    @Override
    public Harvest getHarvestById(UUID harvestId) {
        return harvestRepository.findById(harvestId)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + harvestId));
    }

    @Override
    public Page<Harvest> getAllHarvests(Season season, Integer year, Pageable pageable) {
        if (season == null && year == null) {
            return harvestRepository.findAll(pageable);
        }

        return harvestRepository.findAllBySeasonAndYear(season, year, pageable);
    }

    @Override
    public Harvest updateHarvest(UUID harvestId, HarvestRequestVM request) {
        Harvest existingHarvest = getHarvestById(harvestId);
        existingHarvest.setHarvestDate(request.getHarvestDate());
        existingHarvest.setSeason(determineSeason(request.getHarvestDate()));

        return harvestRepository.save(existingHarvest);
    }

    @Override
    public void deleteHarvest(UUID harvestId) {
        Harvest harvest = getHarvestById(harvestId);

        for (Sales sale : harvest.getSales()) {
            salesService.deleteSale(sale.getId());
        }

        harvestRepository.delete(harvest);
    }

    private Harvest createInitialHarvest(LocalDate harvestDate, Season season) {
        Harvest harvest = new Harvest();
        harvest.setHarvestDate(harvestDate);
        harvest.setTotalQuantity(0.0);
        harvest.setSeason(season);
        return harvestRepository.save(harvest);
    }

    private List<HarvestDetail> generateHarvestDetails(Field field, Harvest harvest, LocalDate harvestDate, Season season) {
        List<HarvestDetail> harvestDetails = new ArrayList<>();
        double totalQuantity = 0.0;

        for (Tree tree : field.getTrees()) {
            validateTreeNotHarvestedThisSeason(tree, season);

            double treeProductivity = calculateTreeProductivityByAge(tree, harvestDate);
            HarvestDetail harvestDetail = createHarvestDetail(harvest, tree, field, treeProductivity);

            harvestDetails.add(harvestDetail);
            totalQuantity += treeProductivity;
        }

        harvestDetailRepository.saveAll(harvestDetails);
        return harvestDetails;
    }

    private HarvestDetail createHarvestDetail(Harvest harvest, Tree tree, Field field, double quantity) {
        HarvestDetail harvestDetail = new HarvestDetail();
        harvestDetail.setHarvest(harvest);
        harvestDetail.setTree(tree);
        harvestDetail.setQuantity(quantity);
        return harvestDetail;
    }

    private Harvest finalizeHarvest(Harvest harvest, List<HarvestDetail> harvestDetails) {
        harvest.setHarvestDetails(harvestDetails);
        harvest.setTotalQuantity(
                harvestDetails.stream()
                        .mapToDouble(HarvestDetail::getQuantity)
                        .sum()
        );
        return harvestRepository.save(harvest);
    }

    private double calculateTreeProductivityByAge(Tree tree, LocalDate harvestDate) {
        int treeAge = Period.between(tree.getPlantingDate(), harvestDate).getYears();

        if (treeAge < 3) {
            return 2.5;
        } else if (treeAge <= 10) {
            return 12.0;
        } else {
            return 20.0;
        }
    }

    private Season determineSeason(LocalDate harvestDate) {
        int month = harvestDate.getDayOfMonth();
        if (month >= 3 && month <= 5) return Season.SPRING;
        if (month >= 6 && month <= 8) return Season.SUMMER;
        if (month >= 9 && month <= 11) return Season.AUTUMN;
        return Season.WINTER;
    }

    private void validateUniqueSeasonHarvest(Field field, Season season) {
        boolean existingHarvest = harvestRepository.existsByFieldIdAndSeason(field.getId(), season);
        if (existingHarvest) {
            throw new HarvestException.DuplicateSeasonHarvestException(season.name());
        }
    }

    private void validateTreeNotHarvestedThisSeason(Tree tree, Season season) {
        boolean treeHarvested = harvestDetailRepository.existsByTreeIdAndHarvestSeason(tree.getId(), season);
        if (treeHarvested) {
            throw new HarvestException.TreeAlreadyHarvestedException(tree.getId());
        }
    }

}
