package org.example.liqouriceproductservice;

import org.example.liqouriceproductservice.config.TestConfig;
import org.example.liqouriceproductservice.models.Product;
import org.example.liqouriceproductservice.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class TestcontainersMongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testMongoConnection() {
        assertNotNull(mongoTemplate);
        assertNotNull(mongoTemplate.getDb());
    }

    @Test
    void testProductRepository() {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setCategories(List.of("Test Category"));
        product.setAmountLeft(10);
        product.setAvailable(true);

        Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct.getId());

        Product retrievedProduct = productRepository.findById(savedProduct.getId()).orElse(null);
        assertNotNull(retrievedProduct);
        assertEquals("Test Product", retrievedProduct.getName());
    }
}
