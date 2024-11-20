package com.javangers.citronix.repository;

import com.javangers.citronix.domain.Harvest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HarvestRepository extends JpaRepository<Harvest, UUID> {

}
