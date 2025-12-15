package com.example.springapp.controller;

import com.example.springapp.dto.ProductDto;
import com.example.springapp.model.Product;
import com.example.springapp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) String q) {
        return service.listAll(q);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductDto dto) {
        Product created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
