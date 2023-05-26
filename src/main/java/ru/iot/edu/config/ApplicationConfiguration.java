package ru.iot.edu.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(
                        new Info()
                                .title("IOT Edu Task Executor microservice api")
                                .version(getClass()
                                        .getPackage()
                                        .getImplementationVersion()
                                )
                                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }

}
