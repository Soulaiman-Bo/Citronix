package com.javangers.citronix.repository;

import com.javangers.citronix.domain.HarvestDetail;
import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.domain.enumeration.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface HarvestDetailRepository extends JpaRepository<HarvestDetail, UUID> {
    Optional<List<HarvestDetail>> findByTree(Tree harvest);
    boolean existsByTreeIdAndHarvestSeason(UUID treeId, Season season);
    void deleteAllByTreeId(UUID treeId);
}