package com.jpmt.powerlab.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Estructura estándar para respuestas de error")
public class ApiErrorResponse {

    @Schema(description = "Detalles de errores de validación (si aplica)", example = "{\"field\": \"must not be null\"}")
    Map<String, String> validationError;
    @Schema(description = "Mensaje descriptivo del error", example = "El recurso solicitado no fue encontrado")
    private String message;
    @Schema(description = "Tipo de error", example = "Resource Not Found")
    private String error;
    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;
    @Schema(description = "Fecha y hora del error")
    private Date date;
}
