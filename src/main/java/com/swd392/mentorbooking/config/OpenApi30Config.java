package com.swd392.mentorbooking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;


//This
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mentor-Booking",
                version = "1.0.0"
        ),
        servers = {
                @Server(url = "https://mentor-booking-api.onrender.com/", description = "Production Server URL"),
                @Server(url = "http://localhost:8080", description = "Development Server URL")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApi30Config {
}
