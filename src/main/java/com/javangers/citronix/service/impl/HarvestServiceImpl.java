package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Field;
import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.HarvestDetail;
import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.repository.HarvestDetailRepository;
import com.javangers.citronix.repository.HarvestRepository;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.service.HarvestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private final  FieldService fieldService;
    private final  HarvestRepository harvestRepository;
    private final  HarvestDetailRepository harvestDetailRepository;

    @Override
    public Harvest createHarvest(LocalDate harvestDate, UUID fields) {

        Field field  = fieldService.getField(fields);

        Harvest harvest = new Harvest();
        harvest.setHarvestDate(harvestDate);
        harvest.setTotalQuantity(0.0);

        harvest = harvestRepository.save(harvest);

        List<Tree> trees =  field.getTrees();

        List<HarvestDetail> harvests = new ArrayList<>();

        double totalQuantity = 0.0;

        Harvest finalHarvest = harvest; // why
        for (Tree tree : trees) {
            HarvestDetail harvestDetail = new HarvestDetail();
            harvestDetail.setHarvest(finalHarvest);
            harvestDetail.setTree(tree);
            double treeProductivity  = calculateTreeProductivityByAge(tree, harvestDate);
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
}
