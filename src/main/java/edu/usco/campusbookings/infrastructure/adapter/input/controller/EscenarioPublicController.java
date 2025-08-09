package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.port.input.EscenarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador público para endpoints de escenarios sin versioning
 * Proporciona endpoints simplificados para el frontend
 */
@RestController
@RequestMapping("/api/escenarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Escenarios Públicos", description = "Endpoints públicos de escenarios para el frontend")
public class EscenarioPublicController {

    private final EscenarioUseCase escenarioUseCase;

    @Operation(summary = "Obtener tipos de escenario", 
               description = "Obtiene todos los tipos únicos de escenarios disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tipos obtenidos exitosamente")
    })
    @GetMapping("/tipos")
    public ResponseEntity<List<String>> getTiposEscenario() {
        log.info("Solicitud para obtener tipos de escenario");
        List<String> tipos = escenarioUseCase.getTiposEscenario();
        log.info("Retornando {} tipos de escenario", tipos.size());
        return ResponseEntity.ok(tipos);
    }

    @Operation(summary = "Obtener ubicaciones de escenario", 
               description = "Obtiene todas las ubicaciones únicas de escenarios disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ubicaciones obtenidas exitosamente")
    })
    @GetMapping("/ubicaciones")
    public ResponseEntity<List<String>> getUbicaciones() {
        log.info("Solicitud para obtener ubicaciones de escenario");
        List<String> ubicaciones = escenarioUseCase.getUbicaciones();
        log.info("Retornando {} ubicaciones de escenario", ubicaciones.size());
        return ResponseEntity.ok(ubicaciones);
    }
}
