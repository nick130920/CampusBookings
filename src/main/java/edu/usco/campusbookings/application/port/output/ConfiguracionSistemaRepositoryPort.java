package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.ConfiguracionSistema;

import java.util.Optional;

public interface ConfiguracionSistemaRepositoryPort {

    /**
     * Obtener la configuración de reservas
     */
    Optional<ConfiguracionSistema> obtenerConfiguracionReservas();

    /**
     * Guardar o actualizar configuración
     */
    ConfiguracionSistema guardarConfiguracion(ConfiguracionSistema configuracion);

    /**
     * Obtener configuración por tipo
     */
    Optional<ConfiguracionSistema> obtenerPorTipo(ConfiguracionSistema.TipoConfiguracion tipo);

    /**
     * Verificar si existe configuración de reservas
     */
    boolean existeConfiguracionReservas();
}