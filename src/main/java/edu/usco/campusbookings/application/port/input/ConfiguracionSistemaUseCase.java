package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ActualizarConfiguracionRequest;
import edu.usco.campusbookings.application.dto.response.ConfiguracionResponse;

public interface ConfiguracionSistemaUseCase {

    /**
     * Obtener la configuración actual de reservas
     */
    ConfiguracionResponse obtenerConfiguracionReservas();

    /**
     * Actualizar la configuración de reservas
     */
    ConfiguracionResponse actualizarConfiguracionReservas(ActualizarConfiguracionRequest request);

    /**
     * Inicializar configuración por defecto si no existe
     */
    ConfiguracionResponse inicializarConfiguracionPorDefecto();
}