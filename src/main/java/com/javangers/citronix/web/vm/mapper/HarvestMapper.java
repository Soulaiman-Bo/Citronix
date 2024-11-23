package com.javangers.citronix.web.vm.mapper;

import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.domain.HarvestDetail;
import com.javangers.citronix.web.vm.request.HarvestDetailResponse;
import com.javangers.citronix.web.vm.request.HarvestRequestVM;
import com.javangers.citronix.web.vm.response.HarvestResponse;
import com.javangers.citronix.web.vm.response.HarvestResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HarvestMapper {

    HarvestResponseVM toHarvestResponseVM(Harvest harvest);

    HarvestResponse toResponse(Harvest harvest);

    @Mapping(source = "tree.id", target = "treeId")
    HarvestDetailResponse toDetailResponse(HarvestDetail harvestDetail);

    List<HarvestResponse> toResponseList(List<Harvest> harvests);

}

