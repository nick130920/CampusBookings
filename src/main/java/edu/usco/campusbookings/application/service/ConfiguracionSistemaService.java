package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ActualizarConfiguracionRequest;
import edu.usco.campusbookings.application.dto.response.ConfiguracionResponse;
import edu.usco.campusbookings.application.exception.ConfiguracionNotFoundException;
import edu.usco.campusbookings.application.port.input.ConfiguracionSistemaUseCase;
import edu.usco.campusbookings.application.port.output.ConfiguracionSistemaRepositoryPort;
import edu.usco.campusbookings.domain.model.ConfiguracionSistema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfiguracionSistemaService implements ConfiguracionSistemaUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionSistemaService.class);

    private final ConfiguracionSistemaRepositoryPort configuracionRepository;

    @Autowired
    public ConfiguracionSistemaService(ConfiguracionSistemaRepositoryPort configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionResponse obtenerConfiguracionReservas() {
        logger.info("Obteniendo configuración de reservas");
        
        ConfiguracionSistema configuracion = configuracionRepository.obtenerConfiguracionReservas()
                .orElseGet(() -> {
                    logger.warn("No se encontró configuración de reservas, creando configuración por defecto");
                    return crearConfiguracionPorDefecto();
                });

        logger.info("Configuración obtenida: días mín={}, días máx={}", 
                   configuracion.getDiasMinimosAnticipacion(), 
                   configuracion.getDiasMaximosAnticipacion());

        return ConfiguracionResponse.fromEntity(configuracion);
    }

    @Override
    public ConfiguracionResponse actualizarConfiguracionReservas(ActualizarConfiguracionRequest request) {
        logger.info("Actualizando configuración de reservas: {}", request);

        // Validar request
        if (!request.isValid()) {
            throw new IllegalArgumentException("Los días mínimos deben ser menores que los días máximos");
        }

        // Obtener configuración existente o crear nueva
        ConfiguracionSistema configuracion = configuracionRepository.obtenerConfiguracionReservas()
                .orElse(new ConfiguracionSistema());

        // Actualizar valores
        configuracion.setDiasMinimosAnticipacion(request.getDiasMinimosAnticipacion());
        configuracion.setDiasMaximosAnticipacion(request.getDiasMaximosAnticipacion());
        configuracion.setDescripcion(request.getDescripcion());
        configuracion.setTipoConfiguracion(ConfiguracionSistema.TipoConfiguracion.RESERVAS);

        // Guardar
        ConfiguracionSistema configuracionGuardada = configuracionRepository.guardarConfiguracion(configuracion);

        logger.info("Configuración actualizada exitosamente: días mín={}, días máx={}", 
                   configuracionGuardada.getDiasMinimosAnticipacion(), 
                   configuracionGuardada.getDiasMaximosAnticipacion());

        return ConfiguracionResponse.fromEntity(configuracionGuardada);
    }

    @Override
    public ConfiguracionResponse inicializarConfiguracionPorDefecto() {
        logger.info("Inicializando configuración por defecto");

        if (configuracionRepository.existeConfiguracionReservas()) {
            logger.info("Ya existe configuración de reservas, obteniendo la existente");
            return obtenerConfiguracionReservas();
        }

        ConfiguracionSistema configuracionPorDefecto = crearConfiguracionPorDefecto();
        ConfiguracionSistema configuracionGuardada = configuracionRepository.guardarConfiguracion(configuracionPorDefecto);

        logger.info("Configuración por defecto creada exitosamente");
        return ConfiguracionResponse.fromEntity(configuracionGuardada);
    }

    private ConfiguracionSistema crearConfiguracionPorDefecto() {
        return new ConfiguracionSistema(
                2, // 2 días mínimos de anticipación
                90, // 90 días máximos de anticipación
                ConfiguracionSistema.TipoConfiguracion.RESERVAS,
                "Configuración por defecto del sistema de reservas"
        );
    }
}