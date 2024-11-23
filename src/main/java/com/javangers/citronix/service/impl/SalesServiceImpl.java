package com.javangers.citronix.service.impl;

import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.Sales;
import com.javangers.citronix.repository.SalesRepository;
import com.javangers.citronix.service.HarvestService;
import com.javangers.citronix.service.SalesService;
import com.javangers.citronix.web.error.ResourceNotFoundException;
import com.javangers.citronix.web.error.SaleException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class SalesServiceImpl implements SalesService {
    private final SalesRepository salesRepository;

    @Autowired
    @Lazy
    private HarvestService harvestService;

    public SalesServiceImpl(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }


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

}
