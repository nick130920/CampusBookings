package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.UsuarioRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioResponse;
import edu.usco.campusbookings.application.port.input.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 /**
 * REST controller for managing Usuario entities.
 * Offers endpoints for CRUD operations and bulk creation.
 * 
 * <p>Endpoints:</p>
 * <ul>
 *   <li>GET /api/usuario/{id}: Retrieve an Usuario by its ID.</li>
 *   <li>GET /api/usuario: Retrieve all Usuario entities.</li>
 *   <li>POST /api/usuario: Create a new Usuario.</li>
 *   <li>POST /api/usuario/bulk: Create multiple Usuario entities in bulk.</li>
 *   <li>PUT /api/usuario/{id}: Update an existing Usuario by its ID.</li>
 *   <li>DELETE /api/usuario/{id}: Delete an Usuario by its ID.</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>UsuarioUseCase: Service for Usuario operations.</li>
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
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuario", description = "Operations related to usuario entities")
public class UsuarioRestController {

    private final UsuarioUseCase usuarioUseCase;

    /**
     * Retrieves a Usuario by its ID.
     * 
     * @param id the Usuario ID
     * @return the found Usuario response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a Usuario by ID", description = "Retrieves a Usuario entity by its unique ID")
    public ResponseEntity<UsuarioResponse> getUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioUseCase.findById(id));
    }

    /**
     * Retrieves all usuarios.
     * 
     * @return the list of Usuario responses
     */
    @GetMapping
    @Operation(summary = "Get all usuarios", description = "Retrieves a list of all Usuario entities")
    public ResponseEntity<List<UsuarioResponse>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioUseCase.findAll());
    }

    /**
     * Creates a new Usuario.
     * 
     * @param usuarioRequest the Usuario request
     * @return the created Usuario response
     */
    @PostMapping
    @Operation(summary = "Create a new Usuario", description = "Creates a new Usuario entity with the provided data")
    public ResponseEntity<UsuarioResponse> createUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.ok(usuarioUseCase.createUsuario(usuarioRequest));
    }

    /**
     * Endpoint to create multiple usuarios in bulk.
     * 
     * @param usuarioRequests List of UsuarioRequest objects to create.
     * @return List of created UsuarioResponse objects.
     */
    @PostMapping("/bulk")
    @Operation(summary = "Create usuarios in bulk", description = "Creates multiple Usuario entities in bulk with the provided data")
    public ResponseEntity<List<UsuarioResponse>> createUsuarios(@RequestBody List<UsuarioRequest> usuarioRequests) {
        return ResponseEntity.ok(usuarioUseCase.createUsuarios(usuarioRequests));
    }

    /**
     * Updates a Usuario by its ID.
     * 
     * @param id the Usuario ID
     * @param usuarioRequest the Usuario request
     * @return the updated Usuario response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Usuario", description = "Updates an existing Usuario entity with the provided data")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.ok(usuarioUseCase.updateUsuario(id, usuarioRequest));
    }

    /**
     * Deletes a Usuario by its ID.
     * 
     * @param id the Usuario ID
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Usuario", description = "Deletes a Usuario entity by its unique ID")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}