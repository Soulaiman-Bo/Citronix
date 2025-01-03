package com.javangers.citronix.web.vm.mapper;


import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.web.vm.request.TreeRequestVM;
import com.javangers.citronix.web.vm.request.TreeUpdateRequestVM;
import com.javangers.citronix.web.vm.response.TreeResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TreeMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "harvestDetails", ignore = true)
    @Mapping(target = "field.id", source = "fieldId")
    Tree toEntity(TreeRequestVM requestVM);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "harvestDetails", ignore = true)
    @Mapping(target = "field.id", source = "fieldId")
    Tree toEntity(TreeUpdateRequestVM requestVM);


    TreeResponseVM toResponseVM(Tree tree);

    List<TreeResponseVM> toResponseVMList(List<Tree> trees);

}