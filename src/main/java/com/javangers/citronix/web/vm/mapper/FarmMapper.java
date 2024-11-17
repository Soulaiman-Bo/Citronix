package com.javangers.citronix.web.vm.mapper;

import com.javangers.citronix.domain.Farm;
import com.javangers.citronix.web.vm.request.FarmRequestVM;
import com.javangers.citronix.web.vm.response.FarmResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FarmMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fields", ignore = true)
    @Mapping(target = "harvests", ignore = true)
    Farm toEntity(FarmRequestVM requestVM);

//    @Mapping(target = "numberOfFields", expression = "java(farm.getFields().size())")
//    @Mapping(target = "numberOfHarvests", expression = "java(farm.getHarvests().size())")
    FarmResponseVM toResponseVM(Farm farm);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fields", ignore = true)
    @Mapping(target = "harvests", ignore = true)
    void updateEntityFromRequest(FarmRequestVM requestVM, @MappingTarget Farm farm);
}