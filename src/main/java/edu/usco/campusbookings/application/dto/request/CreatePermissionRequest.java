package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePermissionRequest {

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "La descripción del permiso es obligatoria")
    @Size(min = 10, max = 255, message = "La descripción debe tener entre 10 y 255 caracteres")
    private String description;

    @NotBlank(message = "El recurso es obligatorio")
    @Size(max = 50, message = "El recurso no debe exceder 50 caracteres")
    private String resource;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 50, message = "La acción no debe exceder 50 caracteres")
    private String action;
}
