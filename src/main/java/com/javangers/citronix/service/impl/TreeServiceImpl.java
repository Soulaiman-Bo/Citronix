package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Field;
import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.repository.TreeRepository;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.service.TreeService;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import com.javangers.citronix.web.error.TreeException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
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
    private final FieldService fieldService;

    public TreeServiceImpl(TreeRepository treeRepository, FieldService fieldService) {
        this.treeRepository = treeRepository;
        this.fieldService = fieldService;
    }


    @Override
    public List<Tree> plantTree(LocalDate plantingDate, UUID fieldId,  Integer quantity) {
        List<Tree> treesList = new ArrayList<>();
        Field field = fieldService.getField(fieldId);

        for (int i = 0; i < quantity; i++) {
            Tree newTree = new Tree();
            newTree.setPlantingDate(plantingDate);
            newTree.setField(field);

            validatePlantingRules(field, newTree);
            treesList.add(newTree);
        }

        return treeRepository.saveAll(treesList);
    }

    private void validatePlantingRules(Field field, Tree tree) {
        // Validate planting season
        int plantingMonth = tree.getPlantingDate().getMonthValue();
        if (plantingMonth < 3 || plantingMonth > 5) {
            throw new TreeException.InvalidPlantingSeasonException();
        }

        // Validate tree density (100 trees per hectare)
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
        Tree existingTree = getTree(treeId);
        validatePlantingRules(existingTree.getField(), updatedTree);
        existingTree.setPlantingDate(updatedTree.getPlantingDate());
        return treeRepository.save(existingTree);
    }

    @Override
    public void removeTree(UUID treeId) {
        Tree tree = getTree(treeId);
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