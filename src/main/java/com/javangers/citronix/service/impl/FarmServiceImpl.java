package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Farm;
import com.javangers.citronix.repository.FarmRepository;
import com.javangers.citronix.service.FarmService;
import com.javangers.citronix.web.error.BusinessRuleViolationException;
import com.javangers.citronix.web.error.FarmException;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import com.javangers.citronix.web.params.FarmSearchParams;
import com.javangers.citronix.web.vm.mapper.FarmMapper;
import com.javangers.citronix.web.vm.request.FarmRequestVM;
import com.javangers.citronix.web.vm.response.FarmResponseVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {
    private final FarmRepository farmRepository;
    private final FarmMapper farmMapper;


    @Override
    public FarmResponseVM createFarm(FarmRequestVM farmRequest) {
        validateFarmRequest(farmRequest);
        validateUniqueFarmName(farmRequest.getName(), farmRequest.getLocation());

        Farm farm = farmMapper.toEntity(farmRequest);
        farm = farmRepository.save(farm);
        return farmMapper.toResponseVM(farm);
    }

    @Override
    public FarmResponseVM getFarm(UUID id) {
        Farm farm = findFarmById(id);
        return farmMapper.toResponseVM(farm);
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
        Farm farm = findFarmById(id);
        if (!farm.getFields().isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Cannot delete farm with existing fields",
                    "FARM_HAS_FIELDS"
            );
        }
        farmRepository.delete(farm);
    }

    @Override
    public Page<FarmResponseVM> searchFarms(FarmSearchParams searchParams, Pageable pageable) {
        List<Farm> farms = farmRepository.searchFarms(
                searchParams.getName(),
                searchParams.getLocation(),
                searchParams.getMinArea(),
                searchParams.getMaxArea(),
                searchParams.getCreatedAfter()
        );

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), farms.size());

        List<Farm> pageContent = farms.subList(start, end);
        return new PageImpl<>(
                pageContent.stream()
                        .map(farmMapper::toResponseVM)
                        .collect(Collectors.toList()),
                pageable,
                farms.size()
        );
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
