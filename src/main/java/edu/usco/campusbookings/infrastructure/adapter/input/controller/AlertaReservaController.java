package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.ConfigurarAlertaRequest;
import edu.usco.campusbookings.application.dto.response.AlertaReservaResponse;
import edu.usco.campusbookings.application.port.input.AlertaReservaUseCase;
import edu.usco.campusbookings.infrastructure.security.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de Alertas de Reservas", description = "Endpoints para gestionar alertas y recordatorios de reservas")
@Slf4j
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaReservaController {

    private final AlertaReservaUseCase alertaReservaUseCase;

    @Operation(summary = "Obtener todas las alertas", 
               description = "Recupera todas las alertas del sistema con paginación")
    @ApiResponse(responseCode = "200", description = "Lista de alertas recuperada exitosamente")
    @GetMapping
    @RequiresPermission(resource = "ALERTS", action = "READ")
    public ResponseEntity<Page<AlertaReservaResponse>> obtenerTodasLasAlertas(
            @Parameter(description = "Número de página (iniciando en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Obteniendo todas las alertas - página: {}, tamaño: {}", page, size);
        
        List<AlertaReservaResponse> todasLasAlertas = alertaReservaUseCase.obtenerTodasLasAlertas();
        
        // Implementar paginación manual
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), todasLasAlertas.size());
        
        List<AlertaReservaResponse> alertasPaginadas = todasLasAlertas.subList(start, end);
        Page<AlertaReservaResponse> alertasPage = new PageImpl<>(alertasPaginadas, pageable, todasLasAlertas.size());
        
        return ResponseEntity.ok(alertasPage);
    }

    @Operation(summary = "Obtener alertas por reserva", 
               description = "Recupera todas las alertas asociadas a una reserva específica")
    @ApiResponse(responseCode = "200", description = "Alertas de reserva recuperadas exitosamente")
    @GetMapping("/reserva/{reservaId}")
    @RequiresPermission(resource = "ALERTS", action = "READ")
    public ResponseEntity<List<AlertaReservaResponse>> obtenerAlertasPorReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long reservaId) {
        
        log.info("Obteniendo alertas para reserva ID: {}", reservaId);
        List<AlertaReservaResponse> alertas = alertaReservaUseCase.obtenerAlertasPorReserva(reservaId);
        return ResponseEntity.ok(alertas);
    }

    @Operation(summary = "Obtener alertas por usuario", 
               description = "Recupera todas las alertas de reservas de un usuario específico")
    @ApiResponse(responseCode = "200", description = "Alertas de usuario recuperadas exitosamente")
    @GetMapping("/usuario/{usuarioId}")
    @RequiresPermission(resource = "ALERTS", action = "READ")
    public ResponseEntity<List<AlertaReservaResponse>> obtenerAlertasPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        
        log.info("Obteniendo alertas para usuario ID: {}", usuarioId);
        List<AlertaReservaResponse> alertas = alertaReservaUseCase.obtenerAlertasPorUsuario(usuarioId);
        return ResponseEntity.ok(alertas);
    }

    @Operation(summary = "Obtener alertas pendientes", 
               description = "Recupera todas las alertas que están pendientes de envío")
    @ApiResponse(responseCode = "200", description = "Alertas pendientes recuperadas exitosamente")
    @GetMapping("/pendientes")
    @RequiresPermission(resource = "ALERTS", action = "READ")
    public ResponseEntity<List<AlertaReservaResponse>> obtenerAlertasPendientes() {
        log.info("Obteniendo alertas pendientes");
        List<AlertaReservaResponse> alertas = alertaReservaUseCase.obtenerAlertasPendientes();
        return ResponseEntity.ok(alertas);
    }

    @Operation(summary = "Configurar alertas personalizadas", 
               description = "Configura alertas personalizadas según los parámetros especificados")
    @ApiResponse(responseCode = "201", description = "Alertas configuradas exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de configuración inválidos")
    @PostMapping("/configurar")
    @RequiresPermission(resource = "ALERTS", action = "CREATE")
    public ResponseEntity<List<AlertaReservaResponse>> configurarAlertas(
            @Valid @RequestBody ConfigurarAlertaRequest request) {
        
        log.info("Configurando alertas personalizadas: {}", request);
        List<AlertaReservaResponse> alertas = alertaReservaUseCase.configurarAlertas(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alertas);
    }

    @Operation(summary = "Enviar alerta manualmente", 
               description = "Envía una alerta específica de forma manual")
    @ApiResponse(responseCode = "200", description = "Alerta enviada exitosamente")
    @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    @ApiResponse(responseCode = "400", description = "Alerta no puede ser enviada en su estado actual")
    @PostMapping("/{alertaId}/enviar")
    @RequiresPermission(resource = "ALERTS", action = "UPDATE")
    public ResponseEntity<AlertaReservaResponse> enviarAlerta(
            @Parameter(description = "ID de la alerta") @PathVariable Long alertaId) {
        
        log.info("Enviando alerta manualmente ID: {}", alertaId);
        AlertaReservaResponse alerta = alertaReservaUseCase.enviarAlerta(alertaId);
        return ResponseEntity.ok(alerta);
    }

    @Operation(summary = "Cancelar alerta", 
               description = "Cancela una alerta pendiente")
    @ApiResponse(responseCode = "200", description = "Alerta cancelada exitosamente")
    @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    @ApiResponse(responseCode = "400", description = "Alerta no puede ser cancelada")
    @PostMapping("/{alertaId}/cancelar")
    @RequiresPermission(resource = "ALERTS", action = "UPDATE")
    public ResponseEntity<AlertaReservaResponse> cancelarAlerta(
            @Parameter(description = "ID de la alerta") @PathVariable Long alertaId) {
        
        log.info("Cancelando alerta ID: {}", alertaId);
        AlertaReservaResponse alerta = alertaReservaUseCase.cancelarAlerta(alertaId);
        return ResponseEntity.ok(alerta);
    }

    @Operation(summary = "Reenviar alerta fallida", 
               description = "Reenvía una alerta que falló anteriormente")
    @ApiResponse(responseCode = "200", description = "Alerta reenviada exitosamente")
    @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    @ApiResponse(responseCode = "400", description = "Alerta no puede ser reenviada")
    @PostMapping("/{alertaId}/reenviar")
    @RequiresPermission(resource = "ALERTS", action = "UPDATE")
    public ResponseEntity<AlertaReservaResponse> reenviarAlerta(
            @Parameter(description = "ID de la alerta") @PathVariable Long alertaId) {
        
        log.info("Reenviando alerta ID: {}", alertaId);
        AlertaReservaResponse alerta = alertaReservaUseCase.reenviarAlerta(alertaId);
        return ResponseEntity.ok(alerta);
    }

    @Operation(summary = "Procesar alertas pendientes", 
               description = "Procesa manualmente todas las alertas pendientes")
    @ApiResponse(responseCode = "200", description = "Alertas procesadas exitosamente")
    @PostMapping("/procesar")
    @RequiresPermission(resource = "ALERTS", action = "UPDATE")
    public ResponseEntity<String> procesarAlertasPendientes() {
        log.info("Procesando alertas pendientes manualmente");
        alertaReservaUseCase.procesarAlertasPendientes();
        return ResponseEntity.ok("Alertas procesadas exitosamente");
    }

    @Operation(summary = "Obtener estadísticas de alertas", 
               description = "Recupera estadísticas detalladas del sistema de alertas")
    @ApiResponse(responseCode = "200", description = "Estadísticas recuperadas exitosamente")
    @GetMapping("/estadisticas")
    @RequiresPermission(resource = "ALERTS", action = "READ")
    public ResponseEntity<AlertaReservaUseCase.EstadisticasAlertas> obtenerEstadisticas() {
        log.info("Obteniendo estadísticas de alertas");
        AlertaReservaUseCase.EstadisticasAlertas estadisticas = alertaReservaUseCase.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @Operation(summary = "Limpiar alertas vencidas", 
               description = "Limpia alertas que han vencido y no fueron procesadas")
    @ApiResponse(responseCode = "200", description = "Alertas vencidas limpiadas exitosamente")
    @PostMapping("/limpiar-vencidas")
    @RequiresPermission(resource = "ALERTS", action = "DELETE")
    public ResponseEntity<String> limpiarAlertasVencidas() {
        log.info("Limpiando alertas vencidas manualmente");
        alertaReservaUseCase.limpiarAlertasVencidas();
        return ResponseEntity.ok("Alertas vencidas limpiadas exitosamente");
    }

    @Operation(summary = "Eliminar alertas de reserva cancelada", 
               description = "Elimina todas las alertas asociadas a una reserva cancelada")
    @ApiResponse(responseCode = "200", description = "Alertas eliminadas exitosamente")
    @DeleteMapping("/reserva/{reservaId}")
    @RequiresPermission(resource = "ALERTS", action = "DELETE")
    public ResponseEntity<String> eliminarAlertasDeReservaCancelada(
            @Parameter(description = "ID de la reserva cancelada") @PathVariable Long reservaId) {
        
        log.info("Eliminando alertas de reserva cancelada ID: {}", reservaId);
        alertaReservaUseCase.eliminarAlertasDeReservaCancelada(reservaId);
        return ResponseEntity.ok("Alertas de reserva cancelada eliminadas exitosamente");
    }
}
