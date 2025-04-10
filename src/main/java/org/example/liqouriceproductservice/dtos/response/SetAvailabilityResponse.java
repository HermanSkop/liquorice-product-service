package org.example.liqouriceproductservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.liqouriceproductservice.dtos.ProductDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetAvailabilityResponse {
    ProductDto product;
}
