package com.javangers.citronix.repository;

import com.javangers.citronix.domain.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface SalesRepository extends JpaRepository<Sales, UUID> {
    Page<Sales> findAll(Pageable pageable);
}