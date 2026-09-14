package com.jpmt.powerlab.models.dto;

import jakarta.persistence.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoNoExponeEntidadesTest {

    private static final String DTO_PACKAGE = "com.jpmt.powerlab.models.dto";

    @Test
    @DisplayName("ningún DTO expone entidades JPA, ni directamente ni dentro de un genérico")
    void ningunDtoExponeEntidadesJpa() {
        List<String> violaciones = new ArrayList<>();

        for (Class<?> dto : dtoClasses()) {
            if (!dto.isRecord()) {
                continue;
            }
            for (RecordComponent componente : dto.getRecordComponents()) {
                for (Class<?> tipo : tiposImplicados(componente.getGenericType())) {
                    if (tipo.isAnnotationPresent(Entity.class)) {
                        violaciones.add("%s.%s expone la entidad %s"
                                .formatted(dto.getSimpleName(), componente.getName(), tipo.getSimpleName()));
                    }
                }
            }
        }

        assertThat(violaciones)
                .as("Los DTOs no pueden exponer entidades JPA. "
                        + "Sustituye la entidad por su id o por un DTO de proyección")
                .isEmpty();
    }

    private List<Class<?>> dtoClasses() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));

        List<Class<?>> clases = new ArrayList<>();
        for (var candidato : scanner.findCandidateComponents(DTO_PACKAGE)) {
            try {
                clases.add(Class.forName(candidato.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("No se pudo cargar " + candidato.getBeanClassName(), e);
            }
        }

        assertThat(clases)
                .as("el escaneo de %s no encontró clases; revisa el nombre del paquete", DTO_PACKAGE)
                .isNotEmpty();
        return clases;
    }

    private List<Class<?>> tiposImplicados(Type tipo) {
        List<Class<?>> tipos = new ArrayList<>();
        if (tipo instanceof Class<?> clase) {
            tipos.add(clase);
        } else if (tipo instanceof ParameterizedType parametrizado) {
            tipos.addAll(tiposImplicados(parametrizado.getRawType()));
            for (Type argumento : parametrizado.getActualTypeArguments()) {
                tipos.addAll(tiposImplicados(argumento));
            }
        }
        return tipos;
    }
}
