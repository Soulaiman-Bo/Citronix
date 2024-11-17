package com.javangers.citronix.web.vm.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmResponseVM {
    private UUID id;
    private String name;
    private String location;
    private Double totalArea;
    private LocalDate creationDate;
//    private int numberOfFields;
//    private int numberOfHarvests;
}