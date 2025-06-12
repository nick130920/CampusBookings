package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.usco.campusbookings.application.dto.request.EscenarioRequest;
import edu.usco.campusbookings.application.dto.response.EscenarioResponse;
import edu.usco.campusbookings.application.port.input.EscenarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

 /**
 * REST controller for managing Escenario entities.
 * Offers endpoints for CRUD operations and bulk creation.
 * 
 * <p>Endpoints:</p>
 * <ul>
 *   <li>GET /api/escenario/{id}: Retrieve an Escenario by its ID.</li>
 *   <li>GET /api/escenario: Retrieve all Escenario entities.</li>
 *   <li>POST /api/escenario: Create a new Escenario.</li>
 *   <li>POST /api/escenario/bulk: Create multiple Escenario entities in bulk.</li>
 *   <li>PUT /api/escenario/{id}: Update an existing Escenario by its ID.</li>
 *   <li>DELETE /api/escenario/{id}: Delete an Escenario by its ID.</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>EscenarioUseCase: Service for Escenario operations.</li>
 * </ul>
 * 
 * <p>Annotations:</p>
 * <ul>
 *   <li>@RestController: Designates this class as a REST controller.</li>
 *   <li>@RequestMapping: Maps HTTP requests to handler methods in this controller.</li>
 *   <li>@RequiredArgsConstructor: Generates a constructor with required arguments.</li>
 *   <li>@Slf4j: Enables logging.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/escenario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Escenario", description = "Operaciones relacionadas con la gestión de escenarios")
public class EscenarioRestController {

    private final EscenarioUseCase escenarioUseCase;

    /**
     * Retrieves a Escenario by its ID.
     * 
     * @param id the Escenario ID
     * @return the found Escenario response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a Escenario by ID", description = "Retrieves a Escenario entity by its unique ID")
    public ResponseEntity<EscenarioResponse> getEscenarioById(@PathVariable Long id) {
        return ResponseEntity.ok(escenarioUseCase.findById(id));
    }

    /**
     * Retrieves all escenarios.
     * 
     * @return the list of Escenario responses
     */
    @GetMapping
    @Operation(summary = "Get all escenarios", description = "Retrieves a list of all Escenario entities")
    public ResponseEntity<List<EscenarioResponse>> getAllEscenarios() {
        return ResponseEntity.ok(escenarioUseCase.findAll());
    }

    /**
     * Creates a new Escenario.
     * 
     * @param escenarioRequest the Escenario request
     * @return the created Escenario response
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new Escenario", description = "Creates a new Escenario entity with the provided data")
    public ResponseEntity<EscenarioResponse> createEscenario(@Valid @RequestBody EscenarioRequest escenarioRequest) {
        return ResponseEntity.ok(escenarioUseCase.createEscenario(escenarioRequest));
    }

    /**
     * Endpoint to create multiple escenarios in bulk.
     * 
     * @param escenarioRequests List of EscenarioRequest objects to create.
     * @return List of created EscenarioResponse objects.
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create escenarios in bulk", description = "Creates multiple Escenario entities in bulk with the provided data")
    public ResponseEntity<List<EscenarioResponse>> createEscenarios(@Valid@RequestBody List<EscenarioRequest> escenarioRequests) {
        return ResponseEntity.ok(escenarioUseCase.createEscenarios(escenarioRequests));
    }

    /**
     * Updates a Escenario by its ID.
     * 
     * @param id the Escenario ID
     * @param escenarioRequest the Escenario request
     * @return the updated Escenario response
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a Escenario", description = "Updates an existing Escenario entity with the provided data")
    public ResponseEntity<EscenarioResponse> updateEscenario(@PathVariable Long id, @RequestBody EscenarioRequest escenarioRequest) {
        return ResponseEntity.ok(escenarioUseCase.updateEscenario(id, escenarioRequest));
    }

    /**
     * Deletes a Escenario by its ID.
     * 
     * @param id the Escenario ID
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Escenario", description = "Deletes a Escenario entity by its unique ID")
    public ResponseEntity<Void> deleteEscenario(@PathVariable Long id) {
        escenarioUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}