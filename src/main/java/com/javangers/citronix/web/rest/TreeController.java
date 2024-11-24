package com.javangers.citronix.web.rest;

import com.javangers.citronix.domain.Tree;
import com.javangers.citronix.service.TreeService;
import com.javangers.citronix.web.vm.mapper.TreeMapper;
import com.javangers.citronix.web.vm.request.TreeRequestVM;
import com.javangers.citronix.web.vm.request.TreeUpdateRequestVM;
import com.javangers.citronix.web.vm.response.TreeResponseVM;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/")
public class TreeController {
    private final TreeService treeService;
    private final TreeMapper treeMapper;

    public TreeController(TreeService treeService, TreeMapper treeMapper) {
        this.treeService = treeService;
        this.treeMapper = treeMapper;
    }


    @PostMapping("trees")
    public ResponseEntity<List<TreeResponseVM>> plantTree(
            @Valid @RequestBody TreeRequestVM requestVM) {
        Tree tree = treeMapper.toEntity(requestVM);
        List<Tree> savedTree = treeService.plantTree(requestVM.getPlantingDate(), requestVM.getFieldId(), requestVM.getQuantity());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(treeMapper.toResponseVMList(savedTree));
    }

    @GetMapping("trees/{treeId}")
    public ResponseEntity<TreeResponseVM> getTree(
            @PathVariable UUID treeId) {
        Tree tree = treeService.getTree(treeId);
        return ResponseEntity.ok(treeMapper.toResponseVM(tree));
    }

    @PutMapping("trees/{treeId}")
    public ResponseEntity<TreeResponseVM> updateTree(
            @PathVariable UUID treeId,
            @Valid @RequestBody TreeUpdateRequestVM requestVM) {
        Tree tree = treeMapper.toEntity(requestVM);
        Tree updatedTree = treeService.updateTree(treeId, tree);
        return ResponseEntity.ok(treeMapper.toResponseVM(updatedTree));
    }

    @DeleteMapping("trees/{treeId}")
    public ResponseEntity<Void> removeTree(
            @PathVariable UUID treeId) {
        treeService.deleteTree(treeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/field/{fieldId}/trees")
    public ResponseEntity<Page<TreeResponseVM>> listTrees(
            @PathVariable UUID fieldId,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String productivityStatus,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Tree> trees = treeService.listFieldTrees(fieldId, age, pageable);
        Page<TreeResponseVM> treeVMs = trees.map(treeMapper::toResponseVM);
        return ResponseEntity.ok(treeVMs);
    }
}

