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
public class ScenarioTypePermissionResponse {
    
    private Long id;
    
    private UsuarioSummary usuario;
    
    private TipoEscenarioSummary tipoEscenario;
    
    private String action;
    
    private Boolean active;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime modifiedDate;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioSummary {
        private Long id;
        private String email;
        private String nombre;
        private String apellido;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TipoEscenarioSummary {
        private Long id;
        private String nombre;
        private String descripcion;
    }
}
