package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.Sales;
import com.javangers.citronix.repository.HarvestRepository;
import com.javangers.citronix.repository.SalesRepository;
import com.javangers.citronix.service.HarvestService;
import com.javangers.citronix.service.SalesService;
import com.javangers.citronix.web.error.BusinessRuleViolationException;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import com.javangers.citronix.web.error.SaleException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {
    private final SalesRepository salesRepository;
//    private final HarvestRepository harvestRepository;
    private final HarvestService harvestService;

    @Override
    public Sales createSale(Sales sale) {
        Harvest harvest = harvestService.getHarvestById(sale.getHarvest().getId());

        validateSale(harvest, sale, null);

        return salesRepository.save(sale);
    }

    @Override
    public Sales updateSale(UUID saleId, Sales updatedSale) {
        Sales existingSale = getSale(saleId);
        Harvest harvest = harvestService.getHarvestById(updatedSale.getHarvest().getId());


        // If harvest is different, validate old and new harvest quantities
        if (!existingSale.getHarvest().getId().equals(updatedSale.getHarvest().getId())) {
            // Release quantity from old harvest
            validateHarvestQuantityAfterRelease(existingSale.getHarvest(), existingSale.getQuantity());
            // Validate quantity for new harvest
            validateSale(harvest, updatedSale, null);
        } else {
            // Same harvest, validate the difference in quantity
            validateSale(harvest, updatedSale, existingSale.getQuantity());
        }

        // Update the existing sale's properties
        existingSale.setQuantity(updatedSale.getQuantity());
        existingSale.setUnitPrice(updatedSale.getUnitPrice());
        existingSale.setClient(updatedSale.getClient());
        existingSale.setSaleDate(updatedSale.getSaleDate());
        existingSale.setHarvest(harvest);

        return salesRepository.save(existingSale);
    }

    @Override
    public void deleteSale(UUID saleId) {
        Sales sale = getSale(saleId);
        validateHarvestQuantityAfterRelease(sale.getHarvest(), sale.getQuantity());
        salesRepository.delete(sale);
    }

    @Override
    public Sales getSale(UUID saleId) {
        return salesRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId.toString()));
    }

    @Override
    public Page<Sales> listSales(Pageable pageable) {
        return salesRepository.findAll(pageable);
    }

    private void validateSale(Harvest harvest, Sales sale, Double excludeQuantity) {
        if (sale.getUnitPrice() <= 0) {
            throw new SaleException.InvalidSalePriceException();
        }

        double totalSoldQuantity = harvest.getSales().stream()
                .mapToDouble(Sales::getQuantity)
                .sum();

        // If we're updating, subtract the original quantity from the total
        if (excludeQuantity != null) {
            totalSoldQuantity -= excludeQuantity;
        }

        if (totalSoldQuantity + sale.getQuantity() > harvest.getTotalQuantity()) {
            throw new SaleException.InsufficientHarvestQuantityException();
        }
    }

    private void validateHarvestQuantityAfterRelease(Harvest harvest, Double releasedQuantity) {
        double totalSoldQuantity = harvest.getSales().stream()
                .mapToDouble(Sales::getQuantity)
                .sum();

        double remainingQuantity = totalSoldQuantity - releasedQuantity;

        if (remainingQuantity < 0) {
            throw new BusinessRuleViolationException(
                    "Invalid state: Remaining quantity would be negative",
                    "INVALID_QUANTITY_STATE"
            );
        }
    }
}
