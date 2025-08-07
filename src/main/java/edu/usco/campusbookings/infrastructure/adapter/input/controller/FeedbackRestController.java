package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.ActualizarFeedbackRequest;
import edu.usco.campusbookings.application.dto.request.CrearFeedbackRequest;
import edu.usco.campusbookings.application.dto.response.EstadisticasFeedbackResponse;
import edu.usco.campusbookings.application.dto.response.FeedbackResponse;
import edu.usco.campusbookings.application.port.input.FeedbackUseCase;
import edu.usco.campusbookings.infrastructure.security.annotation.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller para gestionar las entidades Feedback.
 * Ofrece endpoints para operaciones CRUD y consultas especializadas.
 * 
 * <p>Endpoints:</p>
 * <ul>
 *   <li>POST /api/v1/feedback: Crear un nuevo feedback.</li>
 *   <li>PUT /api/v1/feedback/{id}: Actualizar un feedback existente.</li>
 *   <li>GET /api/v1/feedback/{id}: Obtener un feedback por su ID.</li>
 *   <li>GET /api/v1/feedback/mis-feedbacks: Obtener los feedbacks del usuario autenticado.</li>
 *   <li>GET /api/v1/feedback/escenario/{escenarioId}: Obtener feedbacks de un escenario.</li>
 *   <li>GET /api/v1/feedback/escenario/{escenarioId}/mi-feedback: Obtener el feedback del usuario para un escenario.</li>
 *   <li>GET /api/v1/feedback/escenario/{escenarioId}/estadisticas: Obtener estadísticas de feedback de un escenario.</li>
 *   <li>DELETE /api/v1/feedback/{id}: Eliminar un feedback.</li>
 *   <li>PUT /api/v1/feedback/{id}/desactivar: Desactivar un feedback.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Slf4j
public class FeedbackRestController {

    private final FeedbackUseCase feedbackUseCase;

    /**
     * Crea un nuevo feedback.
     * 
     * @param request el request para crear el feedback
     * @param authentication la autenticación del usuario
     * @return el feedback creado
     */
    @PostMapping
    @RequiresPermission(resource = "FEEDBACK", action = "CREATE")
    public ResponseEntity<FeedbackResponse> crearFeedback(
            @Valid @RequestBody CrearFeedbackRequest request,
            Authentication authentication
    ) {
        log.info("Creando feedback para escenario {} por usuario {}", 
                request.getEscenarioId(), authentication.getName());
        
        FeedbackResponse response = feedbackUseCase.crearFeedback(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza un feedback existente.
     * 
     * @param id el ID del feedback a actualizar
     * @param request el request con los nuevos datos
     * @param authentication la autenticación del usuario
     * @return el feedback actualizado
     */
    @PutMapping("/{id}")
    @RequiresPermission(resource = "FEEDBACK", action = "UPDATE")
    public ResponseEntity<FeedbackResponse> actualizarFeedback(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarFeedbackRequest request,
            Authentication authentication
    ) {
        log.info("Actualizando feedback {} por usuario {}", id, authentication.getName());
        
        FeedbackResponse response = feedbackUseCase.actualizarFeedback(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un feedback por su ID.
     * 
     * @param id el ID del feedback
     * @return el feedback encontrado
     */
    @GetMapping("/{id}")
    @RequiresPermission(resource = "FEEDBACK", action = "READ")
    public ResponseEntity<FeedbackResponse> obtenerFeedbackPorId(@PathVariable Long id) {
        log.debug("Obteniendo feedback por ID: {}", id);
        
        FeedbackResponse response = feedbackUseCase.obtenerFeedbackPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene los feedbacks del usuario autenticado.
     * 
     * @param pageable configuración de paginación
     * @param authentication la autenticación del usuario
     * @return página de feedbacks del usuario
     */
    @GetMapping("/mis-feedbacks")
    @RequiresPermission(resource = "FEEDBACK", action = "READ")
    public ResponseEntity<Page<FeedbackResponse>> obtenerMisFeedbacks(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ) {
        log.debug("Obteniendo feedbacks del usuario: {}", authentication.getName());
        
        Page<FeedbackResponse> response = feedbackUseCase.obtenerMisFeedbacks(authentication.getName(), pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene los feedbacks de un escenario específico.
     * 
     * @param escenarioId el ID del escenario
     * @param pageable configuración de paginación
     * @return página de feedbacks del escenario
     */
    @GetMapping("/escenario/{escenarioId}")
    public ResponseEntity<Page<FeedbackResponse>> obtenerFeedbacksPorEscenario(
            @PathVariable Long escenarioId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.debug("Obteniendo feedbacks para escenario: {}", escenarioId);
        
        Page<FeedbackResponse> response = feedbackUseCase.obtenerFeedbacksPorEscenario(escenarioId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el feedback del usuario autenticado para un escenario específico.
     * 
     * @param escenarioId el ID del escenario
     * @param authentication la autenticación del usuario
     * @return el feedback del usuario para el escenario, o null si no existe
     */
    @GetMapping("/escenario/{escenarioId}/mi-feedback")
    @RequiresPermission(resource = "FEEDBACK", action = "READ")
    public ResponseEntity<FeedbackResponse> obtenerMiFeedbackParaEscenario(
            @PathVariable Long escenarioId,
            Authentication authentication
    ) {
        log.debug("Obteniendo feedback del usuario {} para escenario {}", 
                authentication.getName(), escenarioId);
        
        FeedbackResponse response = feedbackUseCase.obtenerMiFeedbackParaEscenario(escenarioId, authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las estadísticas de feedback de un escenario.
     * 
     * @param escenarioId el ID del escenario
     * @return las estadísticas de feedback del escenario
     */
    @GetMapping("/escenario/{escenarioId}/estadisticas")
    public ResponseEntity<EstadisticasFeedbackResponse> obtenerEstadisticasFeedback(
            @PathVariable Long escenarioId
    ) {
        log.debug("Obteniendo estadísticas de feedback para escenario: {}", escenarioId);
        
        EstadisticasFeedbackResponse response = feedbackUseCase.obtenerEstadisticasFeedback(escenarioId);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un feedback.
     * 
     * @param id el ID del feedback a eliminar
     * @param authentication la autenticación del usuario
     * @return respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "FEEDBACK", action = "DELETE")
    public ResponseEntity<Void> eliminarFeedback(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Eliminando feedback {} por usuario {}", id, authentication.getName());
        
        feedbackUseCase.eliminarFeedback(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Desactiva un feedback.
     * 
     * @param id el ID del feedback a desactivar
     * @param authentication la autenticación del usuario
     * @return respuesta sin contenido
     */
    @PutMapping("/{id}/desactivar")
    @RequiresPermission(resource = "FEEDBACK", action = "UPDATE")
    public ResponseEntity<Void> desactivarFeedback(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Desactivando feedback {} por usuario {}", id, authentication.getName());
        
        feedbackUseCase.desactivarFeedback(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
