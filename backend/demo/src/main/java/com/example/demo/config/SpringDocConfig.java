package com.example.demo.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI demoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo API")
                        .description("Demo API documentation")
                        .version("v1"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("demo")
                .pathsToMatch("/**")
                .build();
    }
}
