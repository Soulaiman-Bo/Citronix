package com.javangers.citronix.web.rest;

import com.javangers.citronix.service.FarmService;
import com.javangers.citronix.web.params.FarmSearchParams;
import com.javangers.citronix.web.vm.request.FarmRequestVM;
import com.javangers.citronix.web.vm.response.FarmResponseVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
public class FarmController {
    private final FarmService farmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FarmResponseVM createFarm(@Valid @RequestBody FarmRequestVM request) {
        return farmService.createFarm(request);
    }

    @GetMapping("/{id}")
    public FarmResponseVM getFarm(@PathVariable UUID id) {
        return farmService.getFarm(id);
    }

    @PutMapping("/{id}")
    public FarmResponseVM updateFarm(
            @PathVariable UUID id,
            @Valid @RequestBody FarmRequestVM request) {
        return farmService.updateFarm(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFarm(@PathVariable UUID id) {
        farmService.deleteFarm(id);
    }

    @GetMapping("/search")
    public Page<FarmResponseVM> searchFarms(
            @Valid FarmSearchParams searchParams,
            @PageableDefault(size = 20) Pageable pageable) {
        return farmService.searchFarms(searchParams, pageable);
    }

}
