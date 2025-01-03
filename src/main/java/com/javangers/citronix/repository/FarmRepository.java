package com.javangers.citronix.repository;


import com.javangers.citronix.domain.Farm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmRepository extends JpaRepository<Farm, UUID> {
    Optional<Farm> findById(UUID id);
    boolean existsByNameAndLocation(String name, String location);

    @Query(value = "SELECT * FROM farms f WHERE " +
                   "(:name IS NULL OR f.name ILIKE CONCAT('%', CAST(:name AS VARCHAR), '%')) AND " +
                   "(:location IS NULL OR f.location ILIKE CONCAT('%', CAST(:location AS VARCHAR), '%')) AND " +
                   "(:minArea IS NULL OR f.total_area >= :minArea) AND " +
                   "(:maxArea IS NULL OR f.total_area <= :maxArea)",
            countQuery = "SELECT COUNT(*) FROM farms f WHERE " +
                         "(:name IS NULL OR f.name ILIKE CONCAT('%', CAST(:name AS VARCHAR), '%')) AND " +
                         "(:location IS NULL OR f.location ILIKE CONCAT('%', CAST(:location AS VARCHAR), '%')) AND " +
                         "(:minArea IS NULL OR f.total_area >= :minArea) AND " +
                         "(:maxArea IS NULL OR f.total_area <= :maxArea)",
            nativeQuery = true)
    Page<Farm> searchFarms(
            @Param("name") String name,
            @Param("location") String location,
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea,
            Pageable pageable
    );
}