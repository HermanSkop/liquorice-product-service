package org.example.liqouriceproductservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.liqouriceproductservice.dtos.ProductDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetProductsResponse {
    private PagedResponse<ProductDto> products;
}
