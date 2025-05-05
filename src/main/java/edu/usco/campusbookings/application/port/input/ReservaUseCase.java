package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;

import java.util.List;

public interface ReservaUseCase {
    ReservaResponse crearReserva(ReservaRequest request);
    ReservaResponse aprobarReserva(Long id);
    ReservaResponse rechazarReserva(Long id);
    ReservaResponse cancelarReserva(Long id);
    List<ReservaResponse> obtenerReservasPorUsuario(Long usuarioId);
    List<ReservaResponse> obtenerReservasPorEscenario(Long escenarioId);
    List<ReservaResponse> obtenerReservasPorEstado(String estadoNombre);
}
