package com.example.springapp.controller;

import com.example.springapp.dto.ProductDto;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void listReturnsOk() throws Exception {
        when(service.listAll(null)).thenReturn(List.of(new Product()));
        mvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test
    void createValidReturnsCreated() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setName("New");
        dto.setPrice(BigDecimal.valueOf(3.5));
        Product created = new Product();
        created.setId(1L);
        when(service.create(any())).thenReturn(created);

        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
