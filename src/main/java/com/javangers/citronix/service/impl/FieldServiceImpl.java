package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Farm;
import com.javangers.citronix.domain.Field;
import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.repository.FarmRepository;
import com.javangers.citronix.repository.FieldRepository;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.service.HarvestDetailsService;
import com.javangers.citronix.service.TreeService;
import com.javangers.citronix.web.error.FarmException;
import com.javangers.citronix.web.error.FieldException;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FieldServiceImpl implements FieldService {
    private final FieldRepository fieldRepository;
    private final FarmRepository farmRepository;

    @Autowired
    @Lazy
    private TreeService treeService;

    // Constructor for required dependencies
    public FieldServiceImpl(FieldRepository fieldRepository,
                            FarmRepository farmRepository) {
        this.fieldRepository = fieldRepository;
        this.farmRepository = farmRepository;
    }

    @Override
    public Field createField(Field field) {
        validateFieldCreation(field);
        return fieldRepository.save(field);
    }

    @Override
    public Field getField(UUID id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field", id.toString()));
    }

    @Override
    public Field updateField(UUID id, Field field) {
        Field existingField = getField(id);
        validateFieldUpdate(field, existingField);

        existingField.setArea(field.getArea());
//        existingField.setName(field.getName());

        return fieldRepository.save(existingField);
    }


    @Override
    public void deleteField(UUID id) {
        Field field = getField(id);

        for (Tree tree : field.getTrees()) {
            treeService.deleteTree(tree.getId());
        }

        fieldRepository.delete(field);
    }

    @Override
    public Page<Field> getAllFieldsByFarm(UUID farmId, Pageable pageable) {
        return new PageImpl<>(
                fieldRepository.findAllByFarmId(farmId, pageable),
                pageable,
                fieldRepository.countByFarmId(farmId)
        );
    }

    private void validateFieldCreation(Field field) {
        Farm farm = farmRepository.findById(field.getFarm().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm", field.getFarm().getId().toString()));

        if (field.getArea() < 0.1) {
            throw new FieldException.FieldAreaTooSmallException();
        }

        if (field.getArea() > farm.getTotalArea() * 0.5) {
            throw new FieldException.FieldAreaTooLargeException();
        }

        long fieldCount = fieldRepository.countByFarmId(farm.getId());
        if (fieldCount >= 10) {
            throw new FarmException.MaxFieldsExceededException();
        }

        double totalFieldsArea = fieldRepository.findAllByFarmId(farm.getId(), Pageable.unpaged())
                .stream()
                .mapToDouble(Field::getArea)
                .sum();

        if (totalFieldsArea + field.getArea() > farm.getTotalArea()) {
            throw new FarmException.TotalAreaExceededException();
        }
    }


    private void validateFieldUpdate(Field newField, Field existingField) {
        // Recalculate total area excluding the existing field's area
        double totalFieldsArea = fieldRepository.findAllByFarmId(existingField.getFarm().getId(), Pageable.unpaged())
                .stream()
                .filter(f -> !f.getId().equals(existingField.getId()))
                .mapToDouble(Field::getArea)
                .sum();

        if (totalFieldsArea + newField.getArea() > existingField.getFarm().getTotalArea()) {
            throw new FarmException.TotalAreaExceededException();
        }

        validateFieldCreation(newField);
    }


}
