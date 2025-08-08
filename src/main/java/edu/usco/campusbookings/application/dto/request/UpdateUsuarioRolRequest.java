package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsuarioRolRequest {
    
    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;
}
