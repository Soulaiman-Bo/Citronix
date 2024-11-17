package com.javangers.citronix.repository;

import com.javangers.citronix.domain.Field;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {
    List<Field> findAllByFarmId(UUID farmId, Pageable pageable);
    long countByFarmId(UUID farmId);
    boolean existsByIdAndTreesIsNotEmpty(UUID id);
}
