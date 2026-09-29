package com.example.parking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI parkingOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Parking System API")
                .version("1.0.0")
                .description("Backend-система парковки: автомобили, места, въезд/выезд и расчёт стоимости"));
    }
}
