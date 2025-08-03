package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.request.VerificarDisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.DisponibilidadResponse;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;

import java.util.List;

public interface ReservaUseCase {
    ReservaResponse crearReserva(ReservaRequest request);
    ReservaResponse aprobarReserva(Long id);
    ReservaResponse rechazarReserva(Long id, String motivo);
    ReservaResponse cancelarReserva(Long id);
    List<ReservaResponse> obtenerReservasPorUsuario(Long usuarioId);
    List<ReservaResponse> obtenerReservasPorEscenario(Long escenarioId);
    List<ReservaResponse> obtenerReservasPorEstado(String estadoNombre);
    List<ReservaResponse> obtenerTodasLasReservas();
    
    /**
     * Verifica la disponibilidad de un escenario en tiempo real.
     * Inspirado en las verificaciones instantáneas de Cal.com.
     */
    DisponibilidadResponse verificarDisponibilidad(VerificarDisponibilidadRequest request);
}
