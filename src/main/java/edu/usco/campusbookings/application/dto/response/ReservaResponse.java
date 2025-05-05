package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {
    private Long id;
    private Long escenarioId;
    private String escenarioNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private Long estadoId;
    private String estadoNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
