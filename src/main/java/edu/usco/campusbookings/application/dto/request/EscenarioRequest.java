package edu.usco.campusbookings.application.dto.request;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO para requests de creación y actualización de escenarios.
 * Incluye validaciones para todos los campos requeridos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscenarioRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    private String tipo;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String ubicacion;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Max(value = 1000, message = "La capacidad no puede exceder 1000 personas")
    private Integer capacidad;

    @Builder.Default
    private Boolean disponible = true;

    @Size(max = 500, message = "Los recursos no pueden exceder 500 caracteres")
    private String recursos;

    @Size(max = 255, message = "La URL de imagen no puede exceder 255 caracteres")
    private String imagenUrl;

    // Campos opcionales para horarios
    private LocalTime horarioApertura;

    private LocalTime horarioCierre;

    @DecimalMin(value = "0.0", message = "El costo por hora no puede ser negativo")
    private Double costoPorHora;

    // Lista de características/recursos como strings
    private List<String> caracteristicas;
}