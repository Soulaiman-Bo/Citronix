package com.javangers.citronix.web.rest;

import com.javangers.citronix.domain.Field;
import com.javangers.citronix.service.FieldService;
import com.javangers.citronix.web.vm.mapper.FieldMapper;
import com.javangers.citronix.web.vm.request.FieldRequestVM;
import com.javangers.citronix.web.vm.response.FieldResponseVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FieldController {
    private final FieldService fieldService;
    private final FieldMapper fieldMapper;

    @PostMapping("/fields")
    @ResponseStatus(HttpStatus.CREATED)
    public FieldResponseVM createField(@Valid @RequestBody FieldRequestVM requestVM) {
        Field field = fieldMapper.toEntity(requestVM);
        return fieldMapper.toVM(fieldService.createField(field));
    }

    @PutMapping("/fields/{id}")
    public FieldResponseVM updateField(
            @PathVariable UUID id,
            @Valid @RequestBody FieldRequestVM requestVM) {
        Field field = fieldMapper.toEntity(requestVM);
        return fieldMapper.toVM(fieldService.updateField(id, field));
    }

    @DeleteMapping("/fields/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteField(@PathVariable UUID id) {
        fieldService.deleteField(id);
    }

    @GetMapping("/farms/{farmId}/fields")
    public Page<FieldResponseVM> getAllFieldsByFarm(
            @PathVariable UUID farmId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return fieldService.getAllFieldsByFarm(farmId, pageable)
                .map(fieldMapper::toVM);
    }

}
