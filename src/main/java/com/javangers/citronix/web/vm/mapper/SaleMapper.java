package com.javangers.citronix.web.vm.mapper;

import com.javangers.citronix.domain.Sales;
import com.javangers.citronix.web.vm.request.SaleRequestVM;
import com.javangers.citronix.web.vm.request.SaleUpdateVM;
import com.javangers.citronix.web.vm.response.SaleResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "harvest.id", source = "harvestId")
    Sales toEntity(SaleRequestVM requestVM);

    @Mapping(source = "harvest.id", target = "harvestId")
    SaleResponseVM toResponseVM(Sales sale);

    List<SaleResponseVM> toResponseVMList(List<Sales> sales);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "harvest.id", source = "harvestId")
    Sales toEntity(SaleUpdateVM updateVM);
}