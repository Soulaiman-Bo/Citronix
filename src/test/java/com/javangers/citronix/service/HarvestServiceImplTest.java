package com.javangers.citronix.service;

import com.javangers.citronix.domain.*;
import com.javangers.citronix.domain.enumeration.Season;
import com.javangers.citronix.repository.HarvestDetailRepository;
import com.javangers.citronix.repository.HarvestRepository;
import com.javangers.citronix.service.impl.HarvestServiceImpl;
import com.javangers.citronix.web.error.BusinessRuleViolationException;
import com.javangers.citronix.web.error.HarvestException;
import com.javangers.citronix.web.vm.request.HarvestRequestVM;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HarvestServiceImplTest {

    @Mock
    private FieldService fieldService;
    @Mock
    private FarmService farmService;
    @Mock
    private HarvestRepository harvestRepository;
    @Mock
    private HarvestDetailRepository harvestDetailRepository;
    @Mock
    private SalesService salesService;

    private HarvestServiceImpl harvestService;

    private Field testField;
    private Farm testFarm;
    private Tree testTree;
    private Harvest testHarvest;
    private HarvestDetail testHarvestDetail;

    @BeforeEach
    void setUp() {
        harvestService = new HarvestServiceImpl(
                fieldService,
                farmService,
                harvestRepository,
                harvestDetailRepository
        );

        // Initialize test data
        testTree = new Tree();
        testTree.setId(UUID.randomUUID());
        testTree.setPlantingDate(LocalDate.now().minusYears(5));

        testField = new Field();
        testField.setId(UUID.randomUUID());
        testField.setTrees(Collections.singletonList(testTree));

        testFarm = new Farm();
        testFarm.setId(UUID.randomUUID());
        testFarm.setFields(Collections.singletonList(testField));

        testHarvest = new Harvest();
        testHarvest.setId(UUID.randomUUID());
        testHarvest.setHarvestDate(LocalDate.now().minusYears(1));
        testHarvest.setSeason(Season.SUMMER);

        testHarvestDetail = new HarvestDetail();
        testHarvestDetail.setId(UUID.randomUUID());
        testHarvestDetail.setHarvest(testHarvest);
        testHarvestDetail.setTree(testTree);
        testHarvestDetail.setQuantity(12.0);
    }

    @Test
    void harvestField_Success() {
        // Arrange
        LocalDate harvestDate = LocalDate.now();
        when(fieldService.getField(any())).thenReturn(testField);
        when(harvestRepository.save(any())).thenReturn(testHarvest);
        when(harvestDetailRepository.saveAll(any())).thenReturn(Collections.singletonList(testHarvestDetail));

        // Act
        Harvest result = harvestService.harvestField(harvestDate, testField.getId());

        // Assert
        assertNotNull(result);
        verify(fieldService).getField(testField.getId());
        verify(harvestRepository, times(2)).save(any());
    }

    @Test
    void harvestField_Not_CurrentYear_ThrowsException() {
        // Arrange
        LocalDate pastYear = LocalDate.now().minusYears(2);

        // Act & Assert
        assertThrows(BusinessRuleViolationException.class,
                () -> harvestService.harvestField(pastYear, testField.getId()));
    }

    @Test
    void harvestField_DuplicateSeasonHarvest_ThrowsException() {
        // Arrange
        LocalDate harvestDate = LocalDate.now();
        when(fieldService.getField(any())).thenReturn(testField);
        when(harvestRepository.existsByFieldIdAndSeason(any(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(HarvestException.DuplicateSeasonHarvestException.class,
                () -> harvestService.harvestField(harvestDate, testField.getId()));
    }

    @Test
    void harvestFarm_Success() {
        // Arrange
        LocalDate harvestDate = LocalDate.now().minusYears(1);
        when(farmService.getFarm(any())).thenReturn(testFarm);
        when(harvestRepository.save(any())).thenReturn(testHarvest);
        when(harvestDetailRepository.saveAll(any())).thenReturn(Collections.singletonList(testHarvestDetail));

        // Act
        Harvest result = harvestService.harvestFarm(harvestDate, testFarm.getId());

        // Assert
        assertNotNull(result);
        verify(farmService).getFarm(testFarm.getId());
        verify(harvestRepository, times(2)).save(any());
    }

    @Test
    void getAllHarvests_WithSeasonAndYear_Success() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Harvest> expectedPage = new PageImpl<>(Collections.singletonList(testHarvest));
        when(harvestRepository.findAllBySeasonAndYear(any(), any(), any())).thenReturn(expectedPage);

        // Act
        Page<Harvest> result = harvestService.getAllHarvests(Season.SUMMER, 2023, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(harvestRepository).findAllBySeasonAndYear(Season.SUMMER, 2023, pageable);
    }

    @Test
    void getAllHarvests_NoFilters_Success() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Harvest> expectedPage = new PageImpl<>(Collections.singletonList(testHarvest));
        when(harvestRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<Harvest> result = harvestService.getAllHarvests(null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(harvestRepository).findAll(pageable);
    }

    @Test
    void updateHarvest_Success() {
        // Arrange
        HarvestRequestVM request = new HarvestRequestVM();
        request.setHarvestDate(LocalDate.now().minusYears(1));

        when(harvestRepository.findById(any())).thenReturn(Optional.of(testHarvest));
        when(harvestRepository.save(any())).thenReturn(testHarvest);

        // Act
        Harvest result = harvestService.updateHarvest(testHarvest.getId(), request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getHarvestDate(), result.getHarvestDate());
        verify(harvestRepository).save(any());
    }

    @Test
    void updateHarvest_NotFound_ThrowsException() {
        // Arrange
        when(harvestRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> harvestService.updateHarvest(UUID.randomUUID(), new HarvestRequestVM()));
    }

//    @Test
//    void deleteHarvest_Success() {
//        // Arrange
//        Sales testSale = new Sales();
//        testSale.setId(UUID.randomUUID());
//        testHarvest.setSales(Collections.singletonList(testSale));
//
//        when(harvestRepository.findById(any())).thenReturn(Optional.of(testHarvest));
//
//        // Act
//        harvestService.deleteHarvest(testHarvest.getId());
//
//        // Assert
//        verify(salesService).deleteSale(testSale.getId());
//        verify(harvestRepository).delete(testHarvest);
//    }

    @Test
    void deleteHarvest_Success() {
        // Arrange
        Sales testSale = new Sales();
        testSale.setId(UUID.randomUUID());
        testHarvest.setSales(Collections.singletonList(testSale));

        // Set the sales service
        harvestService.setSalesService(salesService);

        when(harvestRepository.findById(any())).thenReturn(Optional.of(testHarvest));

        // Act
        harvestService.deleteHarvest(testHarvest.getId());

        // Assert
        verify(salesService).deleteSale(testSale.getId());
        verify(harvestRepository).delete(testHarvest);
    }

}