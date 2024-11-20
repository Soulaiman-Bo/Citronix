package com.javangers.citronix.web.vm.mapper;

import com.javangers.citronix.domain.Harvest;
import com.javangers.citronix.web.vm.response.HarvestResponseVM;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HarvestMapper {

    HarvestResponseVM toHarvestResponseVM(Harvest harvest);
}

