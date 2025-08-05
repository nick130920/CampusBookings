package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.ActualizarConfiguracionRequest;
import edu.usco.campusbookings.application.dto.response.ConfiguracionResponse;
import edu.usco.campusbookings.application.port.input.ConfiguracionSistemaUseCase;
import edu.usco.campusbookings.infrastructure.exception.ErrorResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionSistemaController {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionSistemaController.class);

    private final ConfiguracionSistemaUseCase configuracionUseCase;

    @Autowired
    public ConfiguracionSistemaController(ConfiguracionSistemaUseCase configuracionUseCase) {
        this.configuracionUseCase = configuracionUseCase;
    }

    @GetMapping("/reservas")
               description = "Obtiene la configuración actual de límites para reservas")
    })
    public ResponseEntity<ConfiguracionResponse> obtenerConfiguracionReservas() {
        try {
            logger.info("GET /api/configuracion/reservas - Obteniendo configuración de reservas");
            
            ConfiguracionResponse configuracion = configuracionUseCase.obtenerConfiguracionReservas();
            
            logger.info("Configuración de reservas obtenida exitosamente: {}", configuracion);
            return ResponseEntity.ok(configuracion);
            
        } catch (Exception e) {
            logger.error("Error al obtener configuración de reservas", e);
            throw e;
        }
    }

    @PutMapping("/reservas")
    @PreAuthorize("hasRole('ADMIN')")
               description = "Actualiza los límites de días para realizar reservas")
    })
    public ResponseEntity<ConfiguracionResponse> actualizarConfiguracionReservas(
            @Valid @RequestBody ActualizarConfiguracionRequest request) {
        try {
            logger.info("PUT /api/configuracion/reservas - Actualizando configuración: {}", request);
            
            ConfiguracionResponse configuracion = configuracionUseCase.actualizarConfiguracionReservas(request);
            
            logger.info("Configuración de reservas actualizada exitosamente: {}", configuracion);
            return ResponseEntity.ok(configuracion);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Datos inválidos para actualizar configuración: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar configuración de reservas", e);
            throw e;
        }
    }

    @PostMapping("/reservas/inicializar")
    @PreAuthorize("hasRole('ADMIN')")
               description = "Crea la configuración por defecto si no existe")
    })
    public ResponseEntity<ConfiguracionResponse> inicializarConfiguracionPorDefecto() {
        try {
            logger.info("POST /api/configuracion/reservas/inicializar - Inicializando configuración por defecto");
            
            ConfiguracionResponse configuracion = configuracionUseCase.inicializarConfiguracionPorDefecto();
            
            logger.info("Configuración inicializada exitosamente: {}", configuracion);
            return ResponseEntity.status(HttpStatus.CREATED).body(configuracion);
            
        } catch (Exception e) {
            logger.error("Error al inicializar configuración por defecto", e);
            throw e;
        }
    }
}