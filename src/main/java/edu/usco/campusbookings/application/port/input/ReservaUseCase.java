package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.OcupacionesDiaRequest;
import edu.usco.campusbookings.application.dto.request.OcupacionesMesRequest;
import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.request.VerificarDisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.DisponibilidadResponse;
import edu.usco.campusbookings.application.dto.response.OcupacionesDiaResponse;
import edu.usco.campusbookings.application.dto.response.OcupacionesMesResponse;
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
    
    /**
     * Obtiene todas las ocupaciones de un escenario en un día específico.
     * Optimización para evitar múltiples consultas de disponibilidad.
     */
    OcupacionesDiaResponse obtenerOcupacionesDia(OcupacionesDiaRequest request);
    
    /**
     * Obtiene todas las ocupaciones de un escenario en un mes específico.
     * Optimización para calendarios que necesitan datos de todo el mes.
     */
    OcupacionesMesResponse obtenerOcupacionesMes(OcupacionesMesRequest request);
}
