package com.javangers.citronix.repository;

import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.enumeration.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HarvestRepository extends JpaRepository<Harvest, UUID> {
//    boolean existsByFieldIdAndSeason(UUID fieldId, Season season);

    @Query("SELECT COUNT(h) > 0 FROM Harvest h JOIN h.harvestDetails hd WHERE hd.field.id = :fieldId AND h.season = :season")
    boolean existsByFieldIdAndSeason(@Param("fieldId") UUID fieldId, @Param("season") Season season);

    @Query("SELECT h FROM Harvest h WHERE " +
           "(:season IS NULL OR h.season = :season) AND " +
           "(:year IS NULL OR YEAR(h.harvestDate) = :year)")
    Page<Harvest> findAllBySeasonAndYear(
            @Param("season") Season season,
            @Param("year") Integer year,
            Pageable pageable
    );
}
