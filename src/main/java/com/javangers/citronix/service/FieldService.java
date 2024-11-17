package com.javangers.citronix.service;

import com.javangers.citronix.domain.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FieldService {
    Field createField(Field field);
    Field getField(UUID id);
    Field updateField(UUID id, Field field);
    void deleteField(UUID id);
    Page<Field> getAllFieldsByFarm(UUID farmId, Pageable pageable);
}