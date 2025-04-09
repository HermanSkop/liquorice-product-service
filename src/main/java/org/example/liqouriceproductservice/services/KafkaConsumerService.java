package org.example.liqouriceproductservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.liqouriceproductservice.dtos.request.*;
import org.example.liqouriceproductservice.dtos.response.*;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@KafkaListener(topics = "${kafka.topics.liquorice-product}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaConsumerService {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaHandler
    @SendTo("${kafka.topics.liquorice-product-replies}")
    public GetCategoriesResponse handleGetCategories(@Payload GetCategoriesRequest request, @Headers Map<String, Object> headers) {
        logMessageDetails("GetCategories", request, headers);
        try {
            GetCategoriesResponse response = new GetCategoriesResponse(List.of("success"));
            log.debug("Sending response with {} categories", response.getCategories().size());
            return response;
        } catch (Exception e) {
            log.error("Error processing GetCategories request", e);
            return null;
        }
    }

    @KafkaHandler
    @SendTo("${kafka.topics.liquorice-product-replies}")
    public PagedResponse<ProductPreviewDto> handleGetProducts(@Payload GetProductsRequest request, @Headers Map<String, Object> headers) {
        logMessageDetails("GetProducts", request, headers);
        try {
            PagedResponse<ProductPreviewDto> response = productService.getProductPreviewDtos(
                    request.getPageable(),
                    request.getSearch(),
                    request.getCategories()
            );
            log.debug("Sending response with {} products", response.getContent().size());
            return response;
        } catch (Exception e) {
            log.error("Error processing GetProducts request", e);
            return null;
        }
    }

    @KafkaHandler
    @SendTo("${kafka.topics.liquorice-product-replies}")
    public SetAvailabilityResponse handleSetAvailability(@Payload SetAvailabilityRequest request, @Headers Map<String, Object> headers) {
        logMessageDetails("SetAvailability", request, headers);
        try {
            return productService.setAvailable(request.getProductId(), request.isAvailable())
                    .map(product -> new SetAvailabilityResponse(
                            product.getId(),
                            product.isAvailable(),
                            true,
                            "Product availability updated successfully"
                    ))
                    .orElse(new SetAvailabilityResponse(
                            request.getProductId(),
                            request.isAvailable(),
                            false,
                            "Product not found"
                    ));
        } catch (Exception e) {
            log.error("Error processing SetAvailability request", e);
            return new SetAvailabilityResponse(
                    request.getProductId(),
                    request.isAvailable(),
                    false,
                    "Error processing request: " + e.getMessage()
            );
        }
    }

    private void logMessageDetails(String operation, Object payload, Map<String, Object> headers) {
        log.debug("Received {} request:", operation);
        log.debug("Payload: {}", payload);
        log.debug("Headers:");
        headers.forEach((key, value) -> {
            if (value instanceof byte[]) {
                log.debug("  {}: {}", key, new String((byte[]) value));
            } else {
                log.debug("  {}: {}", key, value);
            }
        });
    }
}