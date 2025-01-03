package com.javangers.citronix.service;


import com.javangers.citronix.domain.Tree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TreeService {
    List<Tree> plantTree(LocalDate plantingDate, UUID fieldId, Integer quantity);
    Tree getTree(UUID treeId);
    Tree updateTree(UUID treeId, Tree tree);
    void deleteTree(UUID treeId);
    Page<Tree> listFieldTrees(UUID fieldId, Integer age, Pageable pageable);
}