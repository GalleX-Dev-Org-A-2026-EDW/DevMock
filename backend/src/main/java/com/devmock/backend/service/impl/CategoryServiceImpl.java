package com.devmock.backend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.CategoryResponse;
import com.devmock.backend.dto.CreateCategoryRequest;
import com.devmock.backend.dto.UpdateCategoryRequest;
import com.devmock.backend.entity.Category;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.exception.SlugAlreadyExistsException;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        if (repository.existsBySlug(request.getSlug())) {
            throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
        }

        Category c = new Category();
        c.setName(request.getName());
        c.setSlug(request.getSlug());
        c.setDescription(request.getDescription());
        c.setIcon(request.getIcon());
        c.setDisplayOrder(request.getDisplayOrder());

        Category saved = repository.save(c);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listActive() {
        return repository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID id) {
        Category c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));
        return toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getBySlug(String slug) {
        Category c = repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category with slug '" + slug + "' not found"));
        return toResponse(c);
    }

    @Override
    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));

        if (request.getName() != null) c.setName(request.getName());

        if (request.getSlug() != null && !request.getSlug().equals(c.getSlug())) {
            if (repository.existsBySlug(request.getSlug())) {
                throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
            }
            c.setSlug(request.getSlug());
        }

        if (request.getDescription() != null) c.setDescription(request.getDescription());
        if (request.getIcon() != null) c.setIcon(request.getIcon());
        if (request.getDisplayOrder() != null) c.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) c.setIsActive(request.getIsActive());

        return toResponse(repository.save(c));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Category " + id + " not found");
        }
        repository.deleteById(id);
    }

    // Mapper
    private CategoryResponse toResponse(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.setId(c.getId());
        r.setName(c.getName());
        r.setSlug(c.getSlug());
        r.setDescription(c.getDescription());
        r.setIcon(c.getIcon());
        r.setDisplayOrder(c.getDisplayOrder());
        r.setIsActive(c.getIsActive());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        return r;
    }
}
