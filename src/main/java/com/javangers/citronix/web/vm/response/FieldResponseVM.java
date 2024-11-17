package com.javangers.citronix.web.vm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldResponseVM {
    private UUID id;
    private Double area;
    private UUID farmId;
//    private String name;
//    private int numberOfTrees;
}