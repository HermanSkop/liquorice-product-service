package org.example.liqouriceproductservice.config.serialization;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

public class DelegatingDeserializer implements Deserializer<Object> {
    
    private final JsonDeserializer<Object> jsonDeserializer;
    
    public DelegatingDeserializer() {
        this.jsonDeserializer = new JsonDeserializer<>();
        this.jsonDeserializer.addTrustedPackages("*");
    }
    
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Map<String, Object> deserializerConfig = new HashMap<>(configs);
        
        // Configure type information
        deserializerConfig.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        deserializerConfig.put(JsonDeserializer.TYPE_MAPPINGS, String.join(",", 
            "getCategoriesRequest:org.example.liqouriceproductservice.dtos.request.GetCategoriesRequest",
            "getProductsRequest:org.example.liqouriceproductservice.dtos.request.GetProductsRequest",
            "setAvailabilityRequest:org.example.liqouriceproductservice.dtos.request.SetAvailabilityRequest"
        ));
        deserializerConfig.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);
        deserializerConfig.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, false);
        
        jsonDeserializer.configure(deserializerConfig, isKey);
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        return jsonDeserializer.deserialize(topic, data);
    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
        return jsonDeserializer.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        jsonDeserializer.close();
    }
}
