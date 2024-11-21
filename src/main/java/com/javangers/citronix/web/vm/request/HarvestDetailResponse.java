package com.javangers.citronix.web.vm.request;

import lombok.Data;

import java.util.UUID;

@Data
public class HarvestDetailResponse {
    private UUID id;
    private Double quantity;
    private UUID fieldId;
    private UUID treeId;
}