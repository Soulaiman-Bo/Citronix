package com.javangers.citronix.service;

import com.javangers.citronix.domain.Farm;
import com.javangers.citronix.domain.Field;
import com.javangers.citronix.repository.FarmRepository;
import com.javangers.citronix.service.impl.FarmServiceImpl;
import com.javangers.citronix.web.error.BusinessRuleViolationException;
import com.javangers.citronix.web.error.FarmException;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import com.javangers.citronix.web.params.FarmSearchParams;
import com.javangers.citronix.web.vm.mapper.FarmMapper;
import com.javangers.citronix.web.vm.request.FarmRequestVM;
import com.javangers.citronix.web.vm.response.FarmResponseVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class FarmServiceImplTest {
    @Mock
    private FarmRepository farmRepository;

    @Mock
    private FarmMapper farmMapper;

    @InjectMocks
    private FarmServiceImpl farmService;

    private Farm testFarm;
    private FarmRequestVM validRequest;
    private FarmResponseVM expectedResponse;
    private UUID farmId;

    @BeforeEach
    void setUp() {
        farmId = UUID.randomUUID();

        testFarm = new Farm();
        testFarm.setId(farmId);
        testFarm.setName("Test Farm");
        testFarm.setLocation("Test Location");
        testFarm.setTotalArea(100.0);
        testFarm.setCreationDate(LocalDate.now());
        testFarm.setFields(Collections.emptyList());

        validRequest = new FarmRequestVM();
        validRequest.setName("Test Farm");
        validRequest.setLocation("Test Location");
        validRequest.setTotalArea(100.0);
        validRequest.setCreationDate(LocalDate.now());

        expectedResponse = new FarmResponseVM();
        expectedResponse.setId(farmId);
        expectedResponse.setName("Test Farm");
        expectedResponse.setLocation("Test Location");
        expectedResponse.setTotalArea(100.0);
        expectedResponse.setCreationDate(LocalDate.now());
    }

    @Test
    void createFarm_Success() {
        when(farmRepository.existsByNameAndLocation(anyString(), anyString())).thenReturn(false);
        when(farmMapper.toEntity(any(FarmRequestVM.class))).thenReturn(testFarm);
        when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
        when(farmMapper.toResponseVM(any(Farm.class))).thenReturn(expectedResponse);

        FarmResponseVM response = farmService.createFarm(validRequest);

        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        verify(farmRepository).save(any(Farm.class));
    }

    @Test
    void createFarm_DuplicateName() {
        when(farmRepository.existsByNameAndLocation(anyString(), anyString())).thenReturn(true);

        assertThrows(FarmException.DuplicateFarmNameException.class,
                () -> farmService.createFarm(validRequest));

        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    void createFarm_InvalidTotalArea() {
        validRequest.setTotalArea(-100.0);

        assertThrows(BusinessRuleViolationException.class,
                () -> farmService.createFarm(validRequest));

        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    void createFarm_FutureDate() {
        validRequest.setCreationDate(LocalDate.now().plusDays(1));

        assertThrows(BusinessRuleViolationException.class,
                () -> farmService.createFarm(validRequest));

        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    void getFarm_Success() {
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(testFarm));

        Farm result = farmService.getFarm(farmId);

        assertNotNull(result);
        assertEquals(testFarm.getId(), result.getId());
    }

    @Test
    void getFarm_NotFound() {
        when(farmRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> farmService.getFarm(UUID.randomUUID()));
    }

    @Test
    void updateFarm_Success() {
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(testFarm));
        when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
        when(farmMapper.toResponseVM(any(Farm.class))).thenReturn(expectedResponse);

        FarmResponseVM response = farmService.updateFarm(farmId, validRequest);

        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        verify(farmMapper).updateEntityFromRequest(eq(validRequest), eq(testFarm));
    }

    @Test
    void updateFarm_NameLocationChanged_Success() {
        validRequest.setName("New Name");
        validRequest.setLocation("New Location");

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(testFarm));
        when(farmRepository.existsByNameAndLocation(anyString(), anyString())).thenReturn(false);
        when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
        when(farmMapper.toResponseVM(any(Farm.class))).thenReturn(expectedResponse);

        FarmResponseVM response = farmService.updateFarm(farmId, validRequest);

        assertNotNull(response);
        verify(farmRepository).existsByNameAndLocation("New Name", "New Location");
    }

    @Test
    void deleteFarm_Success() {
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(testFarm));

        farmService.deleteFarm(farmId);

        verify(farmRepository).delete(testFarm);
    }

    @Test
    void deleteFarm_WithExistingFields() {
        testFarm.setFields(Arrays.asList(new Field()));
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(testFarm));

        assertThrows(BusinessRuleViolationException.class,
                () -> farmService.deleteFarm(farmId));

        verify(farmRepository, never()).delete(any(Farm.class));
    }

    @Test
    void searchFarms_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        FarmSearchParams searchParams = new FarmSearchParams();
        searchParams.setName("Test");
        searchParams.setLocation("Location");
        searchParams.setMinArea(50.0);
        searchParams.setMaxArea(150.0);
        searchParams.setCreatedAfter(LocalDate.now().minusDays(7));

        List<Farm> farmList = Arrays.asList(testFarm);
        when(farmRepository.searchFarms(
                searchParams.getName(),
                searchParams.getLocation(),
                searchParams.getMinArea(),
                searchParams.getMaxArea(),
                searchParams.getCreatedAfter()
        )).thenReturn(farmList);

        when(farmMapper.toResponseVM(any(Farm.class))).thenReturn(expectedResponse);

        Page<FarmResponseVM> result = farmService.searchFarms(searchParams, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(expectedResponse, result.getContent().get(0));
    }

    @Test
    void searchFarms_EmptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        FarmSearchParams searchParams = new FarmSearchParams();

        when(farmRepository.searchFarms(
                null, null, null, null, null
        )).thenReturn(Collections.emptyList());

        Page<FarmResponseVM> result = farmService.searchFarms(searchParams, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }




}
