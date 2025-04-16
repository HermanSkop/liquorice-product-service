package org.example.liqouriceproductservice.services;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.example.liqouriceproductservice.dtos.PagedResponse;
import org.example.liqouriceproductservice.dtos.ProductDto;
import org.example.liqouriceproductservice.models.Product;
import org.example.liqouriceproductservice.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MongoCollection<Document> mongoCollection;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDto testProductDto;
    private List<Product> productList;
    private Page<Product> productPage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("1");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setCategories(List.of("Electronics"));
        testProduct.setAmountLeft(10);
        testProduct.setAvailable(true);
        testProduct.setImage("test-image".getBytes());

        testProductDto = new ProductDto();
        testProductDto.setId("1");
        testProductDto.setName("Test Product");
        testProductDto.setDescription("Test Description");
        testProductDto.setPrice(99.99);
        testProductDto.setCategories(List.of("Electronics"));
        testProductDto.setAmountLeft(10);
        testProductDto.setAvailable(true);
        testProductDto.setImage(Base64.getEncoder().encodeToString("test-image".getBytes()));

        productList = List.of(testProduct);
        pageable = PageRequest.of(0, 10);
        productPage = new PageImpl<>(productList, pageable, 1);
    }

    @Test
    void getProductDtos_WithNoFilters_ShouldReturnAllProducts() {
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(testProductDto);

        PagedResponse<ProductDto> response = productService.getProductDtos(pageable, null, null);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(testProductDto, response.getContent().get(0));
        verify(productRepository).findAll(pageable);
    }

    @Test
    void getProductDtos_WithSearchOnly_ShouldReturnFilteredProducts() {
        when(productRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(testProductDto);

        PagedResponse<ProductDto> response = productService.getProductDtos(pageable, "Test", null);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(productRepository).findByNameContainingIgnoreCase("Test", pageable);
    }

    @Test
    void getProductDtos_WithCategoriesOnly_ShouldReturnFilteredProducts() {
        List<String> categories = List.of("Electronics");
        when(productRepository.findByCategoriesIn(anyList(), any(Pageable.class)))
                .thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(testProductDto);

        PagedResponse<ProductDto> response = productService.getProductDtos(pageable, null, categories);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(productRepository).findByCategoriesIn(categories, pageable);
    }

    @Test
    void getProductDtos_WithSearchAndCategories_ShouldReturnFilteredProducts() {
        List<String> categories = List.of("Electronics");
        when(productRepository.findByNameContainingIgnoreCaseAndCategoriesIn(
                anyString(), anyList(), any(Pageable.class)))
                .thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(testProductDto);

        PagedResponse<ProductDto> response = productService.getProductDtos(pageable, "Test", categories);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(productRepository).findByNameContainingIgnoreCaseAndCategoriesIn("Test", categories, pageable);
    }

    @Test
    void getProductDtos_ByIds_ShouldReturnMatchingProducts() {
        List<String> productIds = List.of("1");
        when(productRepository.findAllById(productIds)).thenReturn(productList);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(testProductDto);

        List<ProductDto> response = productService.getProductDtos(productIds);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testProductDto, response.get(0));
        verify(productRepository).findAllById(productIds);
    }

    @Test
    void getAllCategories_ShouldReturnDistinctCategories() {
        List<String> expectedCategories = List.of("Electronics", "Books");
        when(mongoTemplate.getCollection("products")).thenReturn(mongoCollection);

        DistinctIterable<String> distinctIterable = mock(DistinctIterable.class);
        when(mongoCollection.distinct("categories", String.class)).thenReturn(distinctIterable);
        when(distinctIterable.into(any())).thenReturn(expectedCategories);

        List<String> categories = productService.getAllCategories();

        assertNotNull(categories);
        assertEquals(expectedCategories, categories);
        verify(mongoTemplate).getCollection("products");
        verify(mongoCollection).distinct("categories", String.class);
    }

    @Test
    void mapToProductDto_WithImage_ShouldEncodeImage() {
        when(modelMapper.map(testProduct, ProductDto.class)).thenReturn(testProductDto);

        ProductDto result = productService.mapToProductDto(testProduct);

        assertNotNull(result);
        assertNotNull(result.getImage());
        assertEquals(testProductDto.getImage(), result.getImage());
    }

    @Test
    void mapToProductDto_WithoutImage_ShouldNotEncodeImage() {
        testProduct.setImage(null);
        testProductDto.setImage(null);
        when(modelMapper.map(testProduct, ProductDto.class)).thenReturn(testProductDto);

        ProductDto result = productService.mapToProductDto(testProduct);

        assertNotNull(result);
        assertNull(result.getImage());
    }

    @Test
    void setAvailable_WithExistingProduct_ShouldUpdateAvailability() {
        Product productToUpdate = new Product();
        productToUpdate.setId("1");
        productToUpdate.setAvailable(true);

        ProductDto expectedDto = new ProductDto();
        expectedDto.setId("1");
        expectedDto.setAvailable(false);

        when(productRepository.findById("1")).thenReturn(Optional.of(productToUpdate));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.<Product>getArgument(0));
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(expectedDto);

        Optional<ProductDto> result = productService.setAvailable("1", false);

        assertTrue(result.isPresent());
        assertFalse(result.get().isAvailable());
        verify(productRepository).findById("1");
        verify(productRepository).save(argThat(product -> !product.isAvailable()));
    }

    @Test
    void setAvailable_WithNonExistingProduct_ShouldReturnEmpty() {
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<ProductDto> result = productService.setAvailable("nonexistent", false);

        assertTrue(result.isEmpty());
        verify(productRepository).findById("nonexistent");
        verify(productRepository, never()).save(any());
    }
}