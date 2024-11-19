package com.javangers.citronix.web.vm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreeResponseVM {
    private UUID id;
    private LocalDate plantingDate;
//    private String productivityStatus;
//    private Integer age;
}