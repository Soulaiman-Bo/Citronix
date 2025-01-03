package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Field;
import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.repository.TreeRepository;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.service.HarvestDetailsService;
import com.javangers.citronix.service.TreeService;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import com.javangers.citronix.web.error.TreeException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TreeServiceImpl implements TreeService {
    private final TreeRepository treeRepository;
    private final HarvestDetailsService harvestDetailsService;

    @Autowired
    @Lazy
    private FieldService fieldService;

    // Constructor for required dependencies
    public TreeServiceImpl(TreeRepository treeRepository,
                           HarvestDetailsService harvestDetailsService) {
        this.treeRepository = treeRepository;
        this.harvestDetailsService = harvestDetailsService;
    }


    @Override
    public List<Tree> plantTree(LocalDate plantingDate, UUID fieldId, Integer quantity) {
        List<Tree> treesList = new ArrayList<>();
        Field field = fieldService.getField(fieldId);

        double maxTrees = field.getArea() * 100;

        if (maxTrees < quantity) {
            throw new TreeException.TreeSpacingViolationException();
        }

        for (int i = 0; i < quantity; i++) {
            Tree newTree = new Tree();
            newTree.setPlantingDate(plantingDate);
            newTree.setField(field);

            validatePlatingSeason(newTree);
            validateFieldDensity(field);

            treesList.add(newTree);
        }

        return treeRepository.saveAll(treesList);
    }


    private void validatePlatingSeason(Tree tree){
        int plantingMonth = tree.getPlantingDate().getMonthValue();
        if (plantingMonth < 3 || plantingMonth > 5) {
            throw new TreeException.InvalidPlantingSeasonException();
        }
    }


    private void validateFieldDensity(Field field) {
        long existingTrees = treeRepository.countByFieldId(field.getId());
        double maxTrees = field.getArea() * 100;
        if (existingTrees + 1 > maxTrees) {
            throw new TreeException.TreeSpacingViolationException();
        }
    }

    @Override
    public Tree getTree(UUID treeId) {
        return treeRepository.findById(treeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tree", treeId.toString()));
    }

    @Override
    public Tree updateTree(UUID treeId, Tree updatedTree) {

        validatePlatingSeason(updatedTree);

        Tree existingTree = getTree(treeId);

        // validateFieldDensity(updatedTree.getField());

        existingTree.setPlantingDate(updatedTree.getPlantingDate());
        existingTree.setField(updatedTree.getField());

        return treeRepository.save(existingTree);
    }

    @Override
    public void deleteTree(UUID treeId) {
        Tree tree = getTree(treeId);

        harvestDetailsService.deleteAllByTreeId(treeId);

        treeRepository.delete(tree);
    }

    @Override
    @Transactional
    public Page<Tree> listFieldTrees(UUID fieldId, Integer age, Pageable pageable) {
        fieldService.getField(fieldId);

        if (age != null) {
            LocalDate dateCutoff = LocalDate.now().minusYears(age);
            return treeRepository.findAllByFieldIdAndPlantingDateBetween(
                    fieldId, dateCutoff, LocalDate.now(), pageable);
        }

        return treeRepository.findAllByFieldId(fieldId, pageable);
    }
}