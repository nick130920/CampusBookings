package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.RolRequest;
import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.application.port.input.RolUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 /**
 * REST controller for managing Rol entities.
 * Offers endpoints for CRUD operations and bulk creation.
 * 
 * <p>Endpoints:</p>
 * <ul>
 *   <li>GET /api/rol/{id}: Retrieve an Rol by its ID.</li>
 *   <li>GET /api/rol: Retrieve all Rol entities.</li>
 *   <li>POST /api/rol: Create a new Rol.</li>
 *   <li>POST /api/rol/bulk: Create multiple Rol entities in bulk.</li>
 *   <li>PUT /api/rol/{id}: Update an existing Rol by its ID.</li>
 *   <li>DELETE /api/rol/{id}: Delete an Rol by its ID.</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>RolUseCase: Service for Rol operations.</li>
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
@RequestMapping("/api/rol")
@RequiredArgsConstructor
@Slf4j
public class RolRestController {

    private final RolUseCase rolUseCase;

    /**
     * Retrieves a Rol by its ID.
     * 
     * @param id the Rol ID
     * @return the found Rol response
     */
    @GetMapping("/{id}")
    public ResponseEntity<RolResponse> getRol(@PathVariable Long id) {
        return ResponseEntity.ok(rolUseCase.findById(id));
    }

    /**
     * Retrieves all rols.
     * 
     * @return the list of Rol responses
     */
    @GetMapping
    public ResponseEntity<List<RolResponse>> getAllRols() {
        return ResponseEntity.ok(rolUseCase.findAll());
    }

    /**
     * Creates a new Rol.
     * 
     * @param rolRequest the Rol request
     * @return the created Rol response
     */
    @PostMapping
    public ResponseEntity<RolResponse> createRol(@RequestBody RolRequest rolRequest) {
        return ResponseEntity.ok(rolUseCase.createRol(rolRequest));
    }

    /**
     * Endpoint to create multiple rols in bulk.
     * 
     * @param rolRequests List of RolRequest objects to create.
     * @return List of created RolResponse objects.
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<RolResponse>> createRols(@RequestBody List<RolRequest> rolRequests) {
        return ResponseEntity.ok(rolUseCase.createRols(rolRequests));
    }

    /**
     * Updates a Rol by its ID.
     * 
     * @param id the Rol ID
     * @param rolRequest the Rol request
     * @return the updated Rol response
     */
    @PutMapping("/{id}")
    public ResponseEntity<RolResponse> updateRol(@PathVariable Long id, @RequestBody RolRequest rolRequest) {
        return ResponseEntity.ok(rolUseCase.updateRol(id, rolRequest));
    }

    /**
     * Deletes a Rol by its ID.
     * 
     * @param id the Rol ID
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        rolUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}