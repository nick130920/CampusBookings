package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.DisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.CalendarioDisponibilidadResponse;

import java.util.List;

/**
 * Caso de uso para consultar la disponibilidad de escenarios en formato de calendario.
 * Permite obtener información detallada de disponibilidad por día para un escenario específico.
 */
public interface CalendarioDisponibilidadUseCase {
    
    /**
     * Consulta la disponibilidad de un escenario específico en un rango de fechas.
     * 
     * @param escenarioId ID del escenario a consultar
     * @param request Información del rango de fechas
     * @return Información detallada de disponibilidad por día
     */
    CalendarioDisponibilidadResponse consultarDisponibilidadCalendario(Long escenarioId, DisponibilidadRequest request);
    
    /**
     * Consulta la disponibilidad de múltiples escenarios en un rango de fechas.
     * 
     * @param request Información del rango de fechas y filtros
     * @return Lista de información de disponibilidad por escenario
     */
    List<CalendarioDisponibilidadResponse> consultarDisponibilidadCalendarioMultiple(DisponibilidadRequest request);
} 