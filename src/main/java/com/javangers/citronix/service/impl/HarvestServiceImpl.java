package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Field;
import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.HarvestDetail;
import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.domain.enumeration.Season;
import com.javangers.citronix.repository.HarvestDetailRepository;
import com.javangers.citronix.repository.HarvestRepository;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.service.HarvestService;
import com.javangers.citronix.web.error.HarvestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private final FieldService fieldService;
    private final HarvestRepository harvestRepository;
    private final HarvestDetailRepository harvestDetailRepository;

    @Override
    public Harvest createHarvest(LocalDate harvestDate, UUID fields) {

        Field field = fieldService.getField(fields);

        Season season = determineSeason(harvestDate);

        validateUniqueSeasonHarvest(field, season);

        Harvest harvest = new Harvest();
        harvest.setHarvestDate(harvestDate);
        harvest.setTotalQuantity(0.0);
        harvest.setSeason(season);

        harvest = harvestRepository.save(harvest);

        List<Tree> trees = field.getTrees();
        List<HarvestDetail> harvests = new ArrayList<>();
        double totalQuantity = 0.0;

        Harvest finalHarvest = harvest; // why
        for (Tree tree : trees) {

            validateTreeNotHarvestedThisSeason(tree, season);

            HarvestDetail harvestDetail = new HarvestDetail();
            harvestDetail.setHarvest(finalHarvest);
            harvestDetail.setTree(tree);
            harvestDetail.setField(field);

            double treeProductivity = calculateTreeProductivityByAge(tree, harvestDate);
            harvestDetail.setQuantity(treeProductivity);

            harvests.add(harvestDetail);
            totalQuantity += treeProductivity;
        }

        harvestDetailRepository.saveAll(harvests);

        harvest.setHarvestDetails(harvests);
        harvest.setTotalQuantity(totalQuantity);

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
