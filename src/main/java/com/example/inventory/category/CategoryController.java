package com.example.inventory.category;

import com.example.inventory.common.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable Long id) {
        return CategoryResponse.from(findCategory(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        Category category = new Category();
        apply(request, category);
        Category saved = categoryRepository.save(category);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId()))
                .body(CategoryResponse.from(saved));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        Category category = findCategory(id);
        apply(request, category);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
        return ResponseEntity.noContent().build();
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " was not found"));
    }

    private void apply(CategoryRequest request, Category category) {
        category.setName(request.name());
        category.setDescription(request.description());
    }
}
