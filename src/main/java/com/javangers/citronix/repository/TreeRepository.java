package com.javangers.citronix.repository;


import com.javangers.citronix.domain.Tree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface TreeRepository extends JpaRepository<Tree, UUID> {
    Page<Tree> findAllByFieldId(UUID fieldId, Pageable pageable);
    Page<Tree> findAllByFieldIdAndPlantingDateBetween(UUID fieldId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    long countByFieldId(UUID id);
}
