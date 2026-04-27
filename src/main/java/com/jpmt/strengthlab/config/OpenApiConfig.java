package com.jpmt.strengthlab.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor local de desarrollo"),
                        new Server()
                                .url("https://api.strengthlab.com")
                                .description("Servidor de producción")
                ))
                .info(new Info()
                        .title("StrengthLab API")
                        .version("1.0.0")
                        .description("""
                                API REST para la aplicación de powerlifting **StrengthLab**.
                                
                                **Todos los endpoints devuelven datos en formato JSON**.
                                
                                **Formato de fechas**: ISO 8601 (YYYY-MM-DD)
                                Ejemplo: 2025-04-24
                                
                                **Códigos HTTP principales**:
                                - 200: Éxito (GET, PUT)
                                - 201: Creado (POST)
                                - 204: Sin contenido (DELETE exitoso)
                                - 400: Validación fallida o solicitud inválida
                                - 404: Recurso no encontrado
                                - 500: Error interno del servidor
                                """)
                        .contact(new Contact()
                                .name("StrengthLab Support")
                                .url("https://github.com/jpmt-systems/StrengthLab-Back")
                                .email("support@strengthlab.com"))
                        .termsOfService("https://strengthlab.com/terms")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
