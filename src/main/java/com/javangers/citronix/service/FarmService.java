package com.javangers.citronix.service;


import com.javangers.citronix.domain.Farm;
import com.javangers.citronix.web.params.FarmSearchParams;
import com.javangers.citronix.web.vm.request.FarmRequestVM;
import com.javangers.citronix.web.vm.response.FarmResponseVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.Optional;
import java.util.UUID;


public interface FarmService {
    FarmResponseVM createFarm(FarmRequestVM farmRequest);
    Farm getFarm(UUID id);
    FarmResponseVM updateFarm(UUID id, FarmRequestVM farmRequest);
    void deleteFarm(UUID id);
    Page<FarmResponseVM> searchFarms(FarmSearchParams searchParams, Pageable pageable);
}


