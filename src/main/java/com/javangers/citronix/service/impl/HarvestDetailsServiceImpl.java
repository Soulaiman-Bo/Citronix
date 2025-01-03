package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.HarvestDetail;
import com.javangers.citronix.repository.HarvestDetailRepository;
import com.javangers.citronix.service.HarvestDetailsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Transactional
public class HarvestDetailsServiceImpl implements HarvestDetailsService {
    private final HarvestDetailRepository harvestDetailRepository;

    public HarvestDetailsServiceImpl(HarvestDetailRepository harvestDetailRepository) {
        this.harvestDetailRepository = harvestDetailRepository;
    }

    public void deleteHarvestDetail(UUID id) {
        HarvestDetail harvestDetail = harvestDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("HarvestDetail not found with id: " + id));
        harvestDetailRepository.delete(harvestDetail);
    }

    public void deleteAllByTreeId(UUID treeId) {
        harvestDetailRepository.deleteAllByTreeId(treeId);
    }
}

