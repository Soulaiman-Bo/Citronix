package com.javangers.citronix.service;


import com.javangers.citronix.domain.Tree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TreeService {
    Tree plantTree(Tree tree);
    Tree getTree(UUID treeId);
    Tree updateTree(UUID treeId, Tree tree);
    void removeTree(UUID treeId);
    Page<Tree> listFieldTrees(UUID fieldId, Integer age, Pageable pageable);
}