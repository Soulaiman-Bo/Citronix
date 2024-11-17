package com.javangers.citronix.repository;


import com.javangers.citronix.domain.Farm;
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
    @Query("SELECT f FROM Farm f WHERE " +
           "(:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:location IS NULL OR LOWER(f.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minArea IS NULL OR f.totalArea >= :minArea) AND " +
           "(:maxArea IS NULL OR f.totalArea <= :maxArea) AND " +
           "(:createdAfter IS NULL OR f.creationDate >= :createdAfter)")
    List<Farm> searchFarms(
            @Param("name") String name,
            @Param("location") String location,
            @Param("minArea") Double minArea,
            @Param("maxArea") Double maxArea,
            @Param("createdAfter") LocalDate createdAfter
    );
}
