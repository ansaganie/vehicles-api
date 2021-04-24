package com.udacity.vehicles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2

public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Vehicle API",
                " Vehicles API - a REST API to maintain vehicles data (CRUD) - Pricing Service - a REST API " +
                        "to retrieve the price of a vehicle - Location API - a HTTP client to retrieve the" +
                        " location of the vehicle",
                "1.0",
                "http://www.udacity.com",
                new Contact("Ansagan Islamgali", "www.udacity.com", "ansagan@udacity.com"),
                "License of API", "http://www.udacity.com/license", Collections.emptyList());
    }


}
