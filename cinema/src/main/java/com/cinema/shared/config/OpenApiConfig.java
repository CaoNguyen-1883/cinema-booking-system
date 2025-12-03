package com.cinema.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cinema Booking System API")
                        .version("1.0.0")
                        .description("""
                                API documentation for Cinema Booking System - Modular Monolith Architecture.

                                ## Features
                                - User Authentication & Authorization (JWT)
                                - Movie & Genre Management
                                - Cinema & Hall Management
                                - Show Scheduling
                                - Booking with Seat Selection
                                - VNPay Payment Integration
                                - Email Notifications
                                - QR Code Tickets

                                ## Authentication
                                Use the **Authorize** button to authenticate with JWT token.
                                Get token from `/api/auth/login` endpoint.
                                """)
                        .contact(new Contact()
                                .name("Cinema Booking Team")
                                .email("support@cinemabooking.com")
                                .url("https://cinemabooking.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.cinemabooking.com")
                                .description("Production Server")))
                .tags(Arrays.asList(
                        new Tag().name("Authentication").description("User authentication endpoints"),
                        new Tag().name("Users").description("User management endpoints"),
                        new Tag().name("Movies").description("Movie management endpoints"),
                        new Tag().name("Genres").description("Genre management endpoints"),
                        new Tag().name("Cinemas").description("Cinema and hall management endpoints"),
                        new Tag().name("Shows").description("Show scheduling endpoints"),
                        new Tag().name("Bookings").description("Booking management endpoints"),
                        new Tag().name("Payments").description("Payment processing endpoints"),
                        new Tag().name("Admin").description("Administrative endpoints")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token obtained from login endpoint")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
