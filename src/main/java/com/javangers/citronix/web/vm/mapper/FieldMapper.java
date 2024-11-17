package com.javangers.citronix.web.vm.mapper;

import com.javangers.citronix.domain.Field;
import com.javangers.citronix.web.vm.request.FieldRequestVM;
import com.javangers.citronix.web.vm.response.FieldResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FieldMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trees", ignore = true)
    @Mapping(target = "farm.id", source = "farmId")
    Field toEntity(FieldRequestVM requestVM);

    //    @Mapping(target = "numberOfTrees", expression = "java(field.getTrees() != null ? field.getTrees().size() : 0)")
    @Mapping(target = "farmId", source = "farm.id")
    FieldResponseVM toVM(Field field);

    List<FieldResponseVM> toVMList(List<Field> fields);
}