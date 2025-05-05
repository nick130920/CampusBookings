package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ReporteReservasRequest;
import edu.usco.campusbookings.application.dto.response.ReporteReservasResponse;

import java.util.List;

public interface ReporteReservasUseCase {
    List<ReporteReservasResponse> generarReporte(ReporteReservasRequest request);
}
