package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.DisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.DisponibilidadResponse;

import java.util.List;

public interface DisponibilidadUseCase {
    List<DisponibilidadResponse> consultarDisponibilidad(DisponibilidadRequest request);
}
