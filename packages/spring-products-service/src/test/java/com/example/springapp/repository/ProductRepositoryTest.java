package com.example.springapp.repository;

import com.example.springapp.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        productRepository.deleteAll();
        entityManager.flush();
        
        // Create test products
        product1 = new Product();
        product1.setName("Laptop");
        product1.setDescription("High-performance laptop");
        product1.setPrice(new BigDecimal("999.99"));

        product2 = new Product();
        product2.setName("Mouse");
        product2.setDescription("Wireless mouse");
        product2.setPrice(new BigDecimal("29.99"));

        product3 = new Product();
        product3.setName("Laptop Stand");
        product3.setDescription("Adjustable laptop stand");
        product3.setPrice(new BigDecimal("49.99"));

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();
    }

    @Test
    void findByNameContainingIgnoreCase_WithExactMatch_ReturnsProduct() {
        // When
        List<Product> results = productRepository.findByNameContainingIgnoreCase("Laptop");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Product::getName)
                .containsExactlyInAnyOrder("Laptop", "Laptop Stand");
    }

    @Test
    void findByNameContainingIgnoreCase_WithPartialMatch_ReturnsProduct() {
        // When
        List<Product> results = productRepository.findByNameContainingIgnoreCase("lap");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Product::getName)
                .containsExactlyInAnyOrder("Laptop", "Laptop Stand");
    }

    @Test
    void findByNameContainingIgnoreCase_WithDifferentCase_ReturnsProduct() {
        // When
        List<Product> results = productRepository.findByNameContainingIgnoreCase("LAPTOP");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Product::getName)
                .containsExactlyInAnyOrder("Laptop", "Laptop Stand");
    }

    @Test
    void findByNameContainingIgnoreCase_WithNoMatch_ReturnsEmptyList() {
        // When
        List<Product> results = productRepository.findByNameContainingIgnoreCase("Keyboard");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_WithEmptyString_ReturnsAllProducts() {
        // When
        List<Product> results = productRepository.findByNameContainingIgnoreCase("");

        // Then
        assertThat(results).hasSize(3);
    }

    @Test
    void findById_WithExistingId_ReturnsProduct() {
        // When
        Optional<Product> result = productRepository.findById(product1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop");
        assertThat(result.get().getDescription()).isEqualTo("High-performance laptop");
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("999.99"));
    }

    @Test
    void findById_WithNonExistingId_ReturnsEmpty() {
        // When
        Optional<Product> result = productRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ReturnsAllProducts() {
        // When
        List<Product> results = productRepository.findAll();

        // Then
        assertThat(results).hasSize(3);
        assertThat(results).extracting(Product::getName)
                .containsExactlyInAnyOrder("Laptop", "Mouse", "Laptop Stand");
    }

    @Test
    void save_WithNewProduct_PersistsProduct() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("Keyboard");
        newProduct.setDescription("Mechanical keyboard");
        newProduct.setPrice(new BigDecimal("129.99"));

        // When
        Product savedProduct = productRepository.save(newProduct);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Keyboard");
        assertThat(foundProduct.getDescription()).isEqualTo("Mechanical keyboard");
        assertThat(foundProduct.getPrice()).isEqualByComparingTo(new BigDecimal("129.99"));
    }

    @Test
    void save_WithExistingProduct_UpdatesProduct() {
        // Given
        Product existingProduct = productRepository.findById(product1.getId()).orElseThrow();
        existingProduct.setName("Updated Laptop");
        existingProduct.setPrice(new BigDecimal("899.99"));

        // When
        Product updatedProduct = productRepository.save(existingProduct);
        entityManager.flush();
        entityManager.clear();

        // Then
        Product foundProduct = productRepository.findById(updatedProduct.getId()).orElse(null);
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Updated Laptop");
        assertThat(foundProduct.getPrice()).isEqualByComparingTo(new BigDecimal("899.99"));
    }

    @Test
    void deleteById_WithExistingId_RemovesProduct() {
        // Given
        Long productId = product1.getId();
        assertThat(productRepository.findById(productId)).isPresent();

        // When
        productRepository.deleteById(productId);
        entityManager.flush();

        // Then
        assertThat(productRepository.findById(productId)).isEmpty();
        assertThat(productRepository.findAll()).hasSize(2);
    }

    @Test
    void delete_WithExistingProduct_RemovesProduct() {
        // Given
        assertThat(productRepository.findAll()).hasSize(3);

        // When
        productRepository.delete(product2);
        entityManager.flush();

        // Then
        assertThat(productRepository.findById(product2.getId())).isEmpty();
        assertThat(productRepository.findAll()).hasSize(2);
    }

    @Test
    void count_ReturnsCorrectCount() {
        // When
        long count = productRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void existsById_WithExistingId_ReturnsTrue() {
        // When
        boolean exists = productRepository.existsById(product1.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithNonExistingId_ReturnsFalse() {
        // When
        boolean exists = productRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
