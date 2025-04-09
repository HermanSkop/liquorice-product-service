package org.example.liqouriceproductservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetAvailabilityResponse {
    private String productId;
    private boolean available;
    private boolean success;
    private String message;
}
