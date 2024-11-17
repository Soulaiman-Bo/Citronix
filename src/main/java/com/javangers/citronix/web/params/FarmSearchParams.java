package com.javangers.citronix.web.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FarmSearchParams {
    private String name;
    private String location;
    private Double minArea;
    private Double maxArea;
    private LocalDate createdAfter;
}