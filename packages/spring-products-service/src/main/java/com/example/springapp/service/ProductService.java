package com.example.springapp.service;

import com.example.springapp.dto.ProductDto;
import com.example.springapp.exception.NotFoundException;
import com.example.springapp.model.Product;
import com.example.springapp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) { this.repo = repo; }

    public List<Product> listAll(String q) {
        if (q == null || q.isBlank()) return repo.findAll();
        return repo.findByNameContainingIgnoreCase(q);
    }

    public Product get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public Product create(ProductDto dto) {
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        return repo.save(p);
    }

    public Product update(Long id, ProductDto dto) {
        Product p = get(id);
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        return repo.save(p);
    }

    public void delete(Long id) {
        Product p = get(id);
        repo.delete(p);
    }
}
