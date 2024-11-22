package com.javangers.citronix.service;

import com.javangers.citronix.domain.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface SalesService {
    Sales createSale(Sales sale);
    Sales getSale(UUID saleId);
    Page<Sales> listSales(Pageable pageable);
    Sales updateSale(UUID saleId, Sales updatedSale);
    void deleteSale(UUID saleId);
}