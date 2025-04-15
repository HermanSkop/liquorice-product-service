package org.example.liqouriceproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LiqouriceProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiqouriceProductServiceApplication.class, args);
    }

}
