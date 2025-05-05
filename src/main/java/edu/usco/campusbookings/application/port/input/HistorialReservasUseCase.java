package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.HistorialReservasRequest;
import edu.usco.campusbookings.application.dto.response.HistorialReservasResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HistorialReservasUseCase {
    @PreAuthorize("hasRole('USER')")
    List<HistorialReservasResponse> consultarHistorial(HistorialReservasRequest request);
}
