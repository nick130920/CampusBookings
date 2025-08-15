package edu.usco.campusbookings.infrastructure.adapter.input.rest;

import edu.usco.campusbookings.application.dto.request.ReservaRecurrenteRequest;
import edu.usco.campusbookings.application.dto.response.ReservaRecurrenteResponse;
import edu.usco.campusbookings.application.dto.response.ReservaRecurrenteResumeResponse;
import edu.usco.campusbookings.application.port.input.ReservaRecurrenteUseCase;
import edu.usco.campusbookings.infrastructure.security.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas-recurrentes")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Reservas Recurrentes", description = "APIs para gestión de reservas recurrentes")
public class ReservaRecurrenteController {

    private final ReservaRecurrenteUseCase reservaRecurrenteUseCase;

    @Operation(summary = "Previsualizar reservas recurrentes", 
               description = "Muestra un resumen de las reservas que se generarían con el patrón especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Previsualización generada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @PostMapping("/previsualizar")
    @RequiresPermission(resource = "RESERVA", action = "CREATE")
    public ResponseEntity<ReservaRecurrenteResumeResponse> previsualizarReservasRecurrentes(
            @Valid @RequestBody ReservaRecurrenteRequest request) {
        log.info("Solicitud de previsualización de reservas recurrentes para escenario: {}", request.getEscenarioId());
        
        ReservaRecurrenteResumeResponse resumen = reservaRecurrenteUseCase.previsualizarReservasRecurrentes(request);
        
        return ResponseEntity.ok(resumen);
    }

    @Operation(summary = "Crear reserva recurrente", 
               description = "Crea una nueva configuración de reserva recurrente y genera las reservas iniciales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva recurrente creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos suficientes"),
        @ApiResponse(responseCode = "409", description = "Conflicto con reservas existentes")
    })
    @PostMapping
    @RequiresPermission(resource = "RESERVA", action = "CREATE")
    public ResponseEntity<ReservaRecurrenteResponse> crearReservaRecurrente(
            @Valid @RequestBody ReservaRecurrenteRequest request) {
        log.info("Solicitud de creación de reserva recurrente para escenario: {}", request.getEscenarioId());
        
        ReservaRecurrenteResponse response = reservaRecurrenteUseCase.crearReservaRecurrente(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener reservas recurrentes del usuario", 
               description = "Obtiene todas las reservas recurrentes de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder a los datos del usuario")
    })
    @GetMapping("/usuario/{usuarioId}")
    @RequiresPermission(resource = "RESERVA", action = "READ")
    public ResponseEntity<List<ReservaRecurrenteResponse>> obtenerReservasRecurrentesPorUsuario(
            @Parameter(description = "ID del usuario") 
            @PathVariable @NotNull @Positive Long usuarioId) {
        log.info("Solicitud de reservas recurrentes para usuario: {}", usuarioId);
        
        List<ReservaRecurrenteResponse> reservas = reservaRecurrenteUseCase.obtenerReservasRecurrentesPorUsuario(usuarioId);
        
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Obtener todas las reservas recurrentes (Admin)", 
               description = "Obtiene todas las reservas recurrentes activas del sistema. Solo para administradores.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Se requieren permisos de administrador")
    })
    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(resource = "RESERVA", action = "READ_ALL")
    public ResponseEntity<List<ReservaRecurrenteResponse>> obtenerTodasLasReservasRecurrentes() {
        log.info("Solicitud admin de todas las reservas recurrentes");
        
        List<ReservaRecurrenteResponse> reservas = reservaRecurrenteUseCase.obtenerTodasLasReservasRecurrentes();
        
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Obtener reserva recurrente por ID", 
               description = "Obtiene una reserva recurrente específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva recurrente encontrada"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder a esta reserva"),
        @ApiResponse(responseCode = "404", description = "Reserva recurrente no encontrada")
    })
    @GetMapping("/{id}")
    @RequiresPermission(resource = "RESERVA", action = "READ")
    public ResponseEntity<ReservaRecurrenteResponse> obtenerReservaRecurrentePorId(
            @Parameter(description = "ID de la reserva recurrente") 
            @PathVariable @NotNull @Positive Long id) {
        log.info("Solicitud de reserva recurrente con ID: {}", id);
        
        ReservaRecurrenteResponse reserva = reservaRecurrenteUseCase.obtenerReservaRecurrentePorId(id);
        
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Actualizar reserva recurrente", 
               description = "Actualiza una reserva recurrente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva recurrente actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para modificar esta reserva"),
        @ApiResponse(responseCode = "404", description = "Reserva recurrente no encontrada")
    })
    @PutMapping("/{id}")
    @RequiresPermission(resource = "RESERVA", action = "UPDATE")
    public ResponseEntity<ReservaRecurrenteResponse> actualizarReservaRecurrente(
            @Parameter(description = "ID de la reserva recurrente") 
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody ReservaRecurrenteRequest request) {
        log.info("Solicitud de actualización de reserva recurrente ID: {}", id);
        
        ReservaRecurrenteResponse response = reservaRecurrenteUseCase.actualizarReservaRecurrente(id, request);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desactivar reserva recurrente", 
               description = "Desactiva una reserva recurrente para que no genere más reservas futuras")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva recurrente desactivada exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para modificar esta reserva"),
        @ApiResponse(responseCode = "404", description = "Reserva recurrente no encontrada")
    })
    @PatchMapping("/{id}/desactivar")
    @RequiresPermission(resource = "RESERVA", action = "UPDATE")
    public ResponseEntity<ReservaRecurrenteResponse> desactivarReservaRecurrente(
            @Parameter(description = "ID de la reserva recurrente") 
            @PathVariable @NotNull @Positive Long id) {
        log.info("Solicitud de desactivación de reserva recurrente ID: {}", id);
        
        ReservaRecurrenteResponse response = reservaRecurrenteUseCase.desactivarReservaRecurrente(id);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar reserva recurrente", 
               description = "Activa una reserva recurrente previamente desactivada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva recurrente activada exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para modificar esta reserva"),
        @ApiResponse(responseCode = "404", description = "Reserva recurrente no encontrada")
    })
    @PatchMapping("/{id}/activar")
    @RequiresPermission(resource = "RESERVA", action = "UPDATE")
    public ResponseEntity<ReservaRecurrenteResponse> activarReservaRecurrente(
            @Parameter(description = "ID de la reserva recurrente") 
            @PathVariable @NotNull @Positive Long id) {
        log.info("Solicitud de activación de reserva recurrente ID: {}", id);
        
        ReservaRecurrenteResponse response = reservaRecurrenteUseCase.activarReservaRecurrente(id);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar reserva recurrente", 
               description = "Elimina completamente una reserva recurrente y opcionalmente sus reservas futuras")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reserva recurrente eliminada exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para eliminar esta reserva"),
        @ApiResponse(responseCode = "404", description = "Reserva recurrente no encontrada")
    })
    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "RESERVA", action = "DELETE")
    public ResponseEntity<Void> eliminarReservaRecurrente(
            @Parameter(description = "ID de la reserva recurrente") 
            @PathVariable @NotNull @Positive Long id,
            @Parameter(description = "Si debe eliminar también las reservas futuras generadas") 
            @RequestParam(defaultValue = "false") boolean eliminarReservasFuturas) {
        log.info("Solicitud de eliminación de reserva recurrente ID: {}, eliminar futuras: {}", id, eliminarReservasFuturas);
        
        reservaRecurrenteUseCase.eliminarReservaRecurrente(id, eliminarReservasFuturas);
        
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Generar reservas hasta fecha (Admin)", 
               description = "Genera manualmente reservas para una configuración específica hasta una fecha determinada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservas generadas exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Se requieren permisos de administrador"),
        @ApiResponse(responseCode = "404", description = "Configuración recurrente no encontrada")
    })
    @PostMapping("/{id}/generar-hasta")
    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(resource = "RESERVA", action = "CREATE")
    public ResponseEntity<List<Long>> generarReservasHastaFecha(
            @Parameter(description = "ID de la configuración recurrente") 
            @PathVariable @NotNull @Positive Long id,
            @Parameter(description = "Fecha límite para generar reservas") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite) {
        log.info("Solicitud admin de generación de reservas hasta {} para configuración ID: {}", fechaLimite, id);
        
        List<Long> reservasGeneradas = reservaRecurrenteUseCase.generarReservasHastaFecha(id, fechaLimite);
        
        return ResponseEntity.ok(reservasGeneradas);
    }

    @Operation(summary = "Generar reservas pendientes (Admin)", 
               description = "Procesa todas las configuraciones activas y genera las reservas pendientes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proceso de generación completado"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "403", description = "Se requieren permisos de administrador")
    })
    @PostMapping("/admin/generar-pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(resource = "RESERVA", action = "CREATE")
    public ResponseEntity<String> generarReservasPendientes() {
        log.info("Solicitud admin de generación de reservas pendientes");
        
        reservaRecurrenteUseCase.generarReservasPendientes();
        
        return ResponseEntity.ok("Proceso de generación de reservas pendientes iniciado");
    }
}
