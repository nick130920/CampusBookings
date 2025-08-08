package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDetailResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private RolResponse rol;
    private Integer reservasCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
