package com.javangers.citronix.repository;

import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.HarvestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface HarvestDetailRepository extends JpaRepository<HarvestDetail, UUID> {

}