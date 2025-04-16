package org.example.liqouriceproductservice.controllers;

import lombok.RequiredArgsConstructor;
import org.example.liqouriceproductservice.dtos.ProductDto;
import org.example.liqouriceproductservice.dtos.PagedResponse;
import org.example.liqouriceproductservice.exceptions.NotFoundException;
import org.example.liqouriceproductservice.services.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.liqouriceproductservice.config.AppConfig.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PagedResponse<ProductDto> getProducts(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String sort) {

        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(sort.split(","))
            );
        }

        return productService.getProductDtos(pageable, search, categories);
    }


    @GetMapping("/categories")
    public List<String> getCategories() {
        return productService.getAllCategories();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}/available")
    public ResponseEntity<ProductDto> setAvailable(@RequestBody boolean isAvailable, @PathVariable String productId) {
        return productService.setAvailable(productId, isAvailable)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<ProductDto>> getProductById(@RequestParam String productIds) {
        return new ResponseEntity<>(productService.getProductDtos(List.of(productIds.split(","))), HttpStatus.ACCEPTED);
    }
}
