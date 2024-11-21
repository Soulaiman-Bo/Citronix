package com.javangers.citronix.web.rest;


import com.javangers.citronix.domain.Harvest;

import com.javangers.citronix.service.HarvestService;
import com.javangers.citronix.web.vm.mapper.HarvestMapper;
import com.javangers.citronix.web.vm.request.HarvestRequestVM;
import com.javangers.citronix.web.vm.request.TreeRequestVM;
import com.javangers.citronix.web.vm.response.HarvestResponseVM;
import com.javangers.citronix.web.vm.response.TreeResponseVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HarvestCotroller {
    private final HarvestService harvestService;
    private final HarvestMapper harvestMapper;

    @PostMapping("harvests")
    public ResponseEntity<HarvestResponseVM> plantTree(@Valid @RequestBody HarvestRequestVM requestVM) {

       Harvest harvest = harvestService.createHarvest(requestVM.getHarvestDate(), requestVM.getField());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(harvestMapper.toHarvestResponseVM(harvest));

    }
}
