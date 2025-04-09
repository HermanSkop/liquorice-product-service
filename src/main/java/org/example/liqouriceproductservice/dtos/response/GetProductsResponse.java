package org.example.liqouriceproductservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductsResponse {
    private List<ProductDto> products;
    private long totalElements;
    private int totalPages;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDto {
        private String id;
        private String name;
        private String description;
        private double price;
        private List<String> categories;
        private boolean available;
    }
}
