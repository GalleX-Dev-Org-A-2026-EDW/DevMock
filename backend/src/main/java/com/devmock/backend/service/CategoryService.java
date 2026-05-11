package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CategoryResponse;
import com.devmock.backend.dto.CreateCategoryRequest;
import com.devmock.backend.dto.UpdateCategoryRequest;

public interface CategoryService {

    CategoryResponse create(CreateCategoryRequest request);

    List<CategoryResponse> list();

    List<CategoryResponse> listActive();

    CategoryResponse getById(UUID id);

    CategoryResponse getBySlug(String slug);

    CategoryResponse update(UUID id, UpdateCategoryRequest request);

    void delete(UUID id);
}
