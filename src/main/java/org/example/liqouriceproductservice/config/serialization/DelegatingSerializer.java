package org.example.liqouriceproductservice.config.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class DelegatingSerializer implements Serializer<Object> {
    
    private final JsonSerializer<Object> jsonSerializer;
    private final Map<String, String> typeIdMappings = new HashMap<>();
    
    public DelegatingSerializer() {
        this.jsonSerializer = new JsonSerializer<>();
        
        // Register type mappings
        typeIdMappings.put("org.example.liqouriceproductservice.dtos.response.GetCategoriesResponse", "getCategoriesResponse");
        typeIdMappings.put("org.example.liqouriceproductservice.dtos.response.GetProductsResponse", "getProductsResponse");
        typeIdMappings.put("org.example.liqouriceproductservice.dtos.response.SetAvailabilityResponse", "setAvailabilityResponse");
    }
    
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Map<String, Object> serializerConfig = new HashMap<>(configs);
        
        // Configure type information
        serializerConfig.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        serializerConfig.put(JsonSerializer.TYPE_MAPPINGS, String.join(",", 
            "getCategoriesResponse:org.example.liqouriceproductservice.dtos.response.GetCategoriesResponse",
            "getProductsResponse:org.example.liqouriceproductservice.dtos.response.GetProductsResponse",
            "setAvailabilityResponse:org.example.liqouriceproductservice.dtos.response.SetAvailabilityResponse"
        ));
        
        jsonSerializer.configure(serializerConfig, isKey);
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        return jsonSerializer.serialize(topic, data);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Object data) {
        return jsonSerializer.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        jsonSerializer.close();
    }
}
