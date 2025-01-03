package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Farm;
import com.javangers.citronix.domain.Field;
import com.javangers.citronix.repository.FarmRepository;
import com.javangers.citronix.service.FarmService;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.web.error.BusinessRuleViolationException;
import com.javangers.citronix.web.error.FarmException;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import com.javangers.citronix.web.params.FarmSearchParams;
import com.javangers.citronix.web.vm.mapper.FarmMapper;
import com.javangers.citronix.web.vm.request.FarmRequestVM;
import com.javangers.citronix.web.vm.response.FarmResponseVM;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class FarmServiceImpl implements FarmService {
    private final FarmRepository farmRepository;
    private final FarmMapper farmMapper;

    @Autowired
    @Lazy
    private FieldService fieldService;

    // Constructor for required dependencies
    public FarmServiceImpl(FarmRepository farmRepository, FarmMapper farmMapper) {
        this.farmRepository = farmRepository;
        this.farmMapper = farmMapper;
    }


    @Override
    public FarmResponseVM createFarm(FarmRequestVM farmRequest) {
        validateFarmRequest(farmRequest);
        validateUniqueFarmName(farmRequest.getName(), farmRequest.getLocation());

        Farm farm = farmMapper.toEntity(farmRequest);
        farm = farmRepository.save(farm);
        return farmMapper.toResponseVM(farm);
    }

    @Override
    public Farm getFarm(UUID id) {
        return findFarmById(id);
    }

    @Override
    public FarmResponseVM updateFarm(UUID id, FarmRequestVM farmRequest) {
        Farm existingFarm = findFarmById(id);
        validateFarmRequest(farmRequest);

        if (!existingFarm.getName().equals(farmRequest.getName()) ||
            !existingFarm.getLocation().equals(farmRequest.getLocation())) {
            validateUniqueFarmName(farmRequest.getName(), farmRequest.getLocation());
        }

        farmMapper.updateEntityFromRequest(farmRequest, existingFarm);
        existingFarm = farmRepository.save(existingFarm);
        return farmMapper.toResponseVM(existingFarm);
    }

    @Override
    public void deleteFarm(UUID id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with id: " + id));

        for (Field field : farm.getFields()) {
            fieldService.deleteField(field.getId());
        }

        farmRepository.delete(farm);
    }

    @Override
    public Page<FarmResponseVM> searchFarms(FarmSearchParams searchParams, Pageable pageable) {
        return farmRepository.searchFarms(
                searchParams.getName(),
                searchParams.getLocation(),
                searchParams.getMinArea(),
                searchParams.getMaxArea(),
                pageable
        ).map(farmMapper::toResponseVM);
    }

    private Farm findFarmById(UUID id) {
        return farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm", id.toString()));
    }

    private void validateFarmRequest(FarmRequestVM request) {
        if (request.getTotalArea() <= 0) {
            throw new BusinessRuleViolationException(
                    "Total area must be positive",
                    "INVALID_TOTAL_AREA"
            );
        }

        if (request.getCreationDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleViolationException(
                    "Creation date cannot be in the future",
                    "INVALID_CREATION_DATE"
            );
        }
    }

    private void validateUniqueFarmName(String name, String location) {
        if (farmRepository.existsByNameAndLocation(name, location)) {
            throw new FarmException.DuplicateFarmNameException(name);
        }
    }

}
