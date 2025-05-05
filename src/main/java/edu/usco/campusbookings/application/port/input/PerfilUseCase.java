package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ActualizarPerfilRequest;
import edu.usco.campusbookings.application.dto.response.PerfilResponse;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PerfilUseCase {
    @PreAuthorize("hasRole('USER')")
    PerfilResponse obtenerPerfil();

    @PreAuthorize("hasRole('USER')")
    PerfilResponse actualizarPerfil(ActualizarPerfilRequest request);
}
