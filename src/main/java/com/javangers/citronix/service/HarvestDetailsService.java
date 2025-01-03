package com.javangers.citronix.service;

import java.util.UUID;

public interface HarvestDetailsService {
    public void deleteHarvestDetail(UUID id);
    public void deleteAllByTreeId(UUID treeId);
}
