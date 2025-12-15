package com.example.springapp.controller;

import com.example.springapp.dto.ProductDto;
import com.example.springapp.exception.NotFoundException;
import com.example.springapp.model.Product;
import com.example.springapp.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void listAllProducts_ReturnsOk() throws Exception {
        Product p1 = createProduct(1L, "Laptop", "Gaming laptop", new BigDecimal("1200.00"));
        Product p2 = createProduct(2L, "Mouse", "Wireless mouse", new BigDecimal("25.50"));
        
        when(service.listAll(null)).thenReturn(Arrays.asList(p1, p2));
        
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Mouse")));
        
        verify(service, times(1)).listAll(null);
    }

    @Test
    void listProductsWithQuery_ReturnsFilteredResults() throws Exception {
        Product p1 = createProduct(1L, "Laptop", "Gaming laptop", new BigDecimal("1200.00"));
        
        when(service.listAll("Laptop")).thenReturn(Arrays.asList(p1));
        
        mvc.perform(get("/api/products").param("q", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")));
        
        verify(service, times(1)).listAll("Laptop");
    }

    @Test
    void getProductById_ReturnsProduct() throws Exception {
        Product p = createProduct(1L, "Laptop", "Gaming laptop", new BigDecimal("1200.00"));
        
        when(service.get(1L)).thenReturn(p);
        
        mvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.description", is("Gaming laptop")))
                .andExpect(jsonPath("$.price", is(1200.00)));
        
        verify(service, times(1)).get(1L);
    }

    @Test
    void getProductById_NotFound_Returns404() throws Exception {
        when(service.get(999L)).thenThrow(new NotFoundException("Product not found"));
        
        mvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Product not found")));
        
        verify(service, times(1)).get(999L);
    }

    @Test
    void createValidProduct_ReturnsCreated() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setName("New Product");
        dto.setDescription("New description");
        dto.setPrice(BigDecimal.valueOf(99.99));
        
        Product created = createProduct(1L, "New Product", "New description", new BigDecimal("99.99"));
        when(service.create(any())).thenReturn(created);

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.price", is(99.99)));
        
        verify(service, times(1)).create(any(ProductDto.class));
    }

    @Test
    void createProduct_MissingName_ReturnsBadRequest() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setPrice(BigDecimal.valueOf(99.99));
        
        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
        
        verify(service, never()).create(any());
    }

    @Test
    void createProduct_NegativePrice_ReturnsBadRequest() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setName("Test");
        dto.setPrice(BigDecimal.valueOf(-10.0));
        
        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
        
        verify(service, never()).create(any());
    }

    @Test
    void updateProduct_ReturnsUpdatedProduct() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setName("Updated Product");
        dto.setDescription("Updated description");
        dto.setPrice(BigDecimal.valueOf(149.99));
        
        Product updated = createProduct(1L, "Updated Product", "Updated description", new BigDecimal("149.99"));
        when(service.update(eq(1L), any())).thenReturn(updated);
        
        mvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.price", is(149.99)));
        
        verify(service, times(1)).update(eq(1L), any(ProductDto.class));
    }

    @Test
    void updateProduct_NotFound_Returns404() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setName("Updated");
        dto.setPrice(BigDecimal.valueOf(50.0));
        
        when(service.update(eq(999L), any())).thenThrow(new NotFoundException("Product not found"));
        
        mvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
        
        verify(service, times(1)).update(eq(999L), any(ProductDto.class));
    }

    @Test
    void deleteProduct_ReturnsNoContent() throws Exception {
        doNothing().when(service).delete(1L);
        
        mvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
        
        verify(service, times(1)).delete(1L);
    }

    @Test
    void deleteProduct_NotFound_Returns404() throws Exception {
        doThrow(new NotFoundException("Product not found")).when(service).delete(999L);
        
        mvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
        
        verify(service, times(1)).delete(999L);
    }

    private Product createProduct(Long id, String name, String description, BigDecimal price) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        return p;
    }
}
