package com.javangers.citronix.web.rest;

import com.javangers.citronix.domain.Sales;
import com.javangers.citronix.service.SalesService;
import com.javangers.citronix.web.vm.mapper.SaleMapper;
import com.javangers.citronix.web.vm.request.SaleRequestVM;
import com.javangers.citronix.web.vm.request.SaleUpdateVM;
import com.javangers.citronix.web.vm.response.SaleResponseVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {
    private final SalesService salesService;
    private final SaleMapper saleMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponseVM createSale(@Valid @RequestBody SaleRequestVM requestVM) {
        Sales sale = saleMapper.toEntity(requestVM);
        Sales createdSale = salesService.createSale(sale);
        return saleMapper.toResponseVM(createdSale);
    }

    @GetMapping("/{saleId}")
    public SaleResponseVM getSale(@PathVariable UUID saleId) {
        Sales sale = salesService.getSale(saleId);
        return saleMapper.toResponseVM(sale);
    }

    @GetMapping
    public Page<SaleResponseVM> listSales(
            @PageableDefault(size = 20, sort = "saleDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Sales> sales = salesService.listSales(pageable);
        return sales.map(saleMapper::toResponseVM);
    }

    @PutMapping("/{saleId}")
    public SaleResponseVM updateSale(
            @PathVariable UUID saleId,
            @Valid @RequestBody SaleUpdateVM updateVM) {
        Sales saleToUpdate = saleMapper.toEntity(updateVM);
        Sales updatedSale = salesService.updateSale(saleId, saleToUpdate);
        return saleMapper.toResponseVM(updatedSale);
    }

    @DeleteMapping("/{saleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSale(@PathVariable UUID saleId) {
        salesService.deleteSale(saleId);
    }
}