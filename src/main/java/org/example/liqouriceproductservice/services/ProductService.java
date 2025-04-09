package org.example.liqouriceproductservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.liqouriceproductservice.Product;
import org.example.liqouriceproductservice.ProductRepository;
import org.example.liqouriceproductservice.dtos.response.PagedResponse;
import org.example.liqouriceproductservice.dtos.response.ProductPreviewDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public PagedResponse<ProductPreviewDto> getProductPreviewDtos(Pageable pageable, String search, List<String> categoryNames) {
        Page<Product> productPage;

        if (search != null && !search.isEmpty() && categoryNames != null && !categoryNames.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCaseAndCategoriesIn(search, categoryNames, pageable);
        } else if (search != null && !search.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(search, pageable);
        } else if (categoryNames != null && !categoryNames.isEmpty()) {
            productPage = productRepository.findByCategoriesIn(categoryNames, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductPreviewDto> productDtos = productPage.getContent().stream()
                        .map(this::mapToProductPreviewDto)
                        .collect(Collectors.toList());

        return new PagedResponse<>(
                productDtos,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    public List<String> getAllCategories() {
        return mongoTemplate.getCollection("products")
                .distinct("categories", String.class)
                .into(new ArrayList<>());
    }

    public ProductPreviewDto mapToProductPreviewDto(Product product) {
        ProductPreviewDto dto = modelMapper.map(product, ProductPreviewDto.class);
        if (product.getImage() != null) {
            dto.setImage(Base64.getEncoder().encodeToString(product.getImage()));
        }
        return dto;
    }

    @Transactional
    public Optional<ProductPreviewDto> setAvailable(String productId, boolean isAvailable) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }

        Product product = productOpt.get();
        product.setAvailable(isAvailable);
        return Optional.of(mapToProductPreviewDto(productRepository.save(product)));
    }
}