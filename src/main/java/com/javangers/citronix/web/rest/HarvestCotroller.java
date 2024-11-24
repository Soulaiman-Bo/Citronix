package com.javangers.citronix.web.rest;


import com.javangers.citronix.domain.Harvest;

import com.javangers.citronix.domain.enumeration.Season;
import com.javangers.citronix.service.HarvestService;
import com.javangers.citronix.web.vm.mapper.HarvestMapper;
import com.javangers.citronix.web.vm.request.HarvestRequestVM;
import com.javangers.citronix.web.vm.request.TreeRequestVM;
import com.javangers.citronix.web.vm.response.HarvestResponse;
import com.javangers.citronix.web.vm.response.HarvestResponseVM;
import com.javangers.citronix.web.vm.response.TreeResponseVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HarvestCotroller {
    private final HarvestService harvestService;
    private final HarvestMapper harvestMapper;

    @PostMapping("harvests")
    public ResponseEntity<HarvestResponseVM> HarvestField(@Valid @RequestBody HarvestRequestVM requestVM) {

       Harvest harvest = harvestService.harvestField(requestVM.getHarvestDate(), requestVM.getItem());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(harvestMapper.toHarvestResponseVM(harvest));

    }

    @PostMapping("harvests/all")
    public ResponseEntity<HarvestResponseVM> HarvestFarm(@Valid @RequestBody HarvestRequestVM requestVM) {

        Harvest harvest = harvestService.harvestFarm(requestVM.getHarvestDate(), requestVM.getItem());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(harvestMapper.toHarvestResponseVM(harvest));

    }

    @GetMapping("harvests/{harvestId}")
    public ResponseEntity<HarvestResponse> getHarvest(@PathVariable UUID harvestId) {
        Harvest harvest = harvestService.getHarvestById(harvestId);
        return ResponseEntity.ok(harvestMapper.toResponse(harvest));
    }

    @GetMapping("harvests")
    public ResponseEntity<Page<HarvestResponse>> listHarvests(
            @RequestParam(required = false) Season season,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "harvestDate,desc") String[] sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrders(sort)));
        Page<Harvest> harvests = harvestService.getAllHarvests(season, year, pageable);
        return ResponseEntity.ok(harvests.map(harvestMapper::toResponse));
    }

    private List<Sort.Order> getSortOrders(String[] sort) {
        return Arrays.stream(sort)
                .map(sortParam -> {
                    String[] parts = sortParam.split(",");
                    String property = parts[0];
                    Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc")
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;
                    return new Sort.Order(direction, property);
                })
                .collect(Collectors.toList());
    }

    @PutMapping("harvests/{harvestId}")
    public ResponseEntity<HarvestResponse> updateHarvest(
            @PathVariable UUID harvestId,
            @Valid @RequestBody HarvestRequestVM request
    ) {
        Harvest updatedHarvest = harvestService.updateHarvest(harvestId, request);
        return ResponseEntity.ok(harvestMapper.toResponse(updatedHarvest));
    }

    @DeleteMapping("harvests/{harvestId}")
    public ResponseEntity<Void> deleteHarvest(@PathVariable UUID harvestId) {
        harvestService.deleteHarvest(harvestId);
        return ResponseEntity.noContent().build();
    }


}
