package com.jpmt.powerlab.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Servidor de swagger")

                ))
                .info(new Info()
                        .title("PowerLab API")
                        .version("1.0.0")
                        .description("""
                                API REST de **PowerLab**, plataforma de gestión de entrenamiento de fuerza.

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
                                .name("PowerLab Support")
                                .url("https://github.com/jpmt-systems/PowerLab-Back")
                                .email("support@powerlab.com"))
                        .termsOfService("https://powerlab.com/terms")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
