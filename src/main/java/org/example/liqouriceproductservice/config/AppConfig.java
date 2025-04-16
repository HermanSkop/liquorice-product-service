package org.example.liqouriceproductservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
public class AppConfig {
    public final static String BASE_PATH = "/api/v1";
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
