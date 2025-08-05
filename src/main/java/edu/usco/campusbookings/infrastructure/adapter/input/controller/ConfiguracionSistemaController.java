package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.ActualizarConfiguracionRequest;
import edu.usco.campusbookings.application.dto.response.ConfiguracionResponse;
import edu.usco.campusbookings.application.port.input.ConfiguracionSistemaUseCase;
import edu.usco.campusbookings.infrastructure.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Configuración del Sistema", description = "API para gestionar la configuración del sistema")
@CrossOrigin(origins = "*")
public class ConfiguracionSistemaController {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionSistemaController.class);

    private final ConfiguracionSistemaUseCase configuracionUseCase;

    @Autowired
    public ConfiguracionSistemaController(ConfiguracionSistemaUseCase configuracionUseCase) {
        this.configuracionUseCase = configuracionUseCase;
    }

    @GetMapping("/reservas")
    @Operation(summary = "Obtener configuración de reservas", 
               description = "Obtiene la configuración actual de límites para reservas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuración obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ConfiguracionResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
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
    @Operation(summary = "Actualizar configuración de reservas", 
               description = "Actualiza los límites de días para realizar reservas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ConfiguracionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requieren permisos de administrador"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
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
    @Operation(summary = "Inicializar configuración por defecto", 
               description = "Crea la configuración por defecto si no existe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Configuración inicializada exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ConfiguracionResponse.class))),
        @ApiResponse(responseCode = "200", description = "Configuración ya existe",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ConfiguracionResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requieren permisos de administrador"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
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