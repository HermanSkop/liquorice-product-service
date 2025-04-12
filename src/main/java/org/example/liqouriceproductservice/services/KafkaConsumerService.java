package org.example.liqouriceproductservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.liqouriceproductservice.dtos.ProductDto;
import org.example.liqouriceproductservice.dtos.request.*;
import org.example.liqouriceproductservice.dtos.response.*;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaHandler
    @SendTo("${kafka.topics.liquorice-product-replies}")
    public GetCategoriesResponse handleGetCategories(@Payload GetCategoriesRequest request, @Headers Map<String, Object> headers) {
        logMessageDetails("GetCategories", request, headers);
        GetCategoriesResponse response = new GetCategoriesResponse(List.of("success6"));
        log.debug("Sending response with {} categories", response.getCategories().size());
        return response;
    }

    @KafkaHandler
    @SendTo("${kafka.topics.liquorice-product-replies}")
    public GetProductsResponse handleGetProducts(@Payload GetProductsRequest request, @Headers Map<String, Object> headers) {
        /*logMessageDetails("GetProducts", request, headers);
        PagedResponse<ProductDto> response = productService.getProductDtos(
                request.getSearch(),
                request.getCategories(),
                request.getPageNumber(),
                request.getPageSize(),
                request.getSort()
        );*//*

        log.debug("Sending response with {} products", response.getContent().size());
        return new GetProductsResponse(response);*/

        return null;
    }

    @KafkaHandler
    @SendTo("${kafka.topics.liquorice-product-replies}")
    public SetAvailabilityResponse handleSetAvailability(@Payload SetAvailabilityRequest request, @Headers Map<String, Object> headers) {
        logMessageDetails("SetAvailability", request, headers);
        return productService.setAvailable(request.getProductId(), request.isAvailable())
                .map(SetAvailabilityResponse::new)
                .orElseThrow();
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