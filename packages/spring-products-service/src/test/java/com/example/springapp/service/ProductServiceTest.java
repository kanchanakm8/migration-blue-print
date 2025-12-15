package com.example.springapp.service;

import com.example.springapp.dto.ProductDto;
import com.example.springapp.exception.NotFoundException;
import com.example.springapp.model.Product;
import com.example.springapp.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product testProduct;
    private ProductDto testDto;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));

        testDto = new ProductDto();
        testDto.setName("New Product");
        testDto.setDescription("New Description");
        testDto.setPrice(new BigDecimal("49.99"));
    }

    @Test
    void listAll_WithoutQuery_ReturnsAllProducts() {
        Product p1 = createProduct(1L, "Product 1");
        Product p2 = createProduct(2L, "Product 2");
        when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> result = service.listAll(null);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(p1, p2);
        verify(repository, times(1)).findAll();
        verify(repository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void listAll_WithBlankQuery_ReturnsAllProducts() {
        Product p1 = createProduct(1L, "Product 1");
        when(repository.findAll()).thenReturn(Arrays.asList(p1));

        List<Product> result = service.listAll("   ");

        assertThat(result).hasSize(1);
        verify(repository, times(1)).findAll();
    }

    @Test
    void listAll_WithQuery_ReturnsFilteredProducts() {
        Product p1 = createProduct(1L, "Laptop");
        when(repository.findByNameContainingIgnoreCase("Laptop")).thenReturn(Arrays.asList(p1));

        List<Product> result = service.listAll("Laptop");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        verify(repository, times(1)).findByNameContainingIgnoreCase("Laptop");
        verify(repository, never()).findAll();
    }

    @Test
    void get_ExistingId_ReturnsProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = service.get(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void get_NonExistingId_ThrowsNotFoundException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product not found");

        verify(repository, times(1)).findById(999L);
    }

    @Test
    void create_ValidDto_ReturnsCreatedProduct() {
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName(testDto.getName());
        savedProduct.setDescription(testDto.getDescription());
        savedProduct.setPrice(testDto.getPrice());

        when(repository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = service.create(testDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("New Product");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("49.99"));
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void update_ExistingProduct_ReturnsUpdatedProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(repository.save(any(Product.class))).thenReturn(testProduct);

        Product result = service.update(1L, testDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Product");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("49.99"));
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(testProduct);
    }

    @Test
    void update_NonExistingProduct_ThrowsNotFoundException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, testDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product not found");

        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_ExistingProduct_DeletesProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(repository).delete(testProduct);

        service.delete(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(testProduct);
    }

    @Test
    void delete_NonExistingProduct_ThrowsNotFoundException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product not found");

        verify(repository, times(1)).findById(999L);
        verify(repository, never()).delete(any());
    }

    private Product createProduct(Long id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setDescription("Description for " + name);
        p.setPrice(new BigDecimal("100.00"));
        return p;
    }
}
