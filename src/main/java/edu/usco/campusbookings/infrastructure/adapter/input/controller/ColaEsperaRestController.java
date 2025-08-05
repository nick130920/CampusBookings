package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.ColaEsperaRequest;
import edu.usco.campusbookings.application.dto.response.ColaEsperaResponse;
import edu.usco.campusbookings.application.port.input.ColaEsperaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 /**
 * REST controller for managing ColaEspera entities.
 * Offers endpoints for CRUD operations and bulk creation.
 * 
 * <p>Endpoints:</p>
 * <ul>
 *   <li>GET /api/colaespera/{id}: Retrieve an ColaEspera by its ID.</li>
 *   <li>GET /api/colaespera: Retrieve all ColaEspera entities.</li>
 *   <li>POST /api/colaespera: Create a new ColaEspera.</li>
 *   <li>POST /api/colaespera/bulk: Create multiple ColaEspera entities in bulk.</li>
 *   <li>PUT /api/colaespera/{id}: Update an existing ColaEspera by its ID.</li>
 *   <li>DELETE /api/colaespera/{id}: Delete an ColaEspera by its ID.</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>ColaEsperaUseCase: Service for ColaEspera operations.</li>
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
@RequestMapping("/api/colaespera")
@RequiredArgsConstructor
@Slf4j
public class ColaEsperaRestController {

    private final ColaEsperaUseCase colaesperaUseCase;

    /**
     * Retrieves a ColaEspera by its ID.
     * 
     * @param id the ColaEspera ID
     * @return the found ColaEspera response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ColaEsperaResponse> getColaEspera(@PathVariable Long id) {
        return ResponseEntity.ok(colaesperaUseCase.findById(id));
    }

    /**
     * Retrieves all colaesperas.
     * 
     * @return the list of ColaEspera responses
     */
    @GetMapping
    public ResponseEntity<List<ColaEsperaResponse>> getAllColaEsperas() {
        return ResponseEntity.ok(colaesperaUseCase.findAll());
    }

    /**
     * Creates a new ColaEspera.
     * 
     * @param colaesperaRequest the ColaEspera request
     * @return the created ColaEspera response
     */
    @PostMapping
    public ResponseEntity<ColaEsperaResponse> createColaEspera(@RequestBody ColaEsperaRequest colaesperaRequest) {
        return ResponseEntity.ok(colaesperaUseCase.createColaEspera(colaesperaRequest));
    }

    /**
     * Endpoint to create multiple colaesperas in bulk.
     * 
     * @param colaesperaRequests List of ColaEsperaRequest objects to create.
     * @return List of created ColaEsperaResponse objects.
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<ColaEsperaResponse>> createColaEsperas(@RequestBody List<ColaEsperaRequest> colaesperaRequests) {
        return ResponseEntity.ok(colaesperaUseCase.createColaEsperas(colaesperaRequests));
    }

    /**
     * Updates a ColaEspera by its ID.
     * 
     * @param id the ColaEspera ID
     * @param colaesperaRequest the ColaEspera request
     * @return the updated ColaEspera response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ColaEsperaResponse> updateColaEspera(@PathVariable Long id, @RequestBody ColaEsperaRequest colaesperaRequest) {
        return ResponseEntity.ok(colaesperaUseCase.updateColaEspera(id, colaesperaRequest));
    }

    /**
     * Deletes a ColaEspera by its ID.
     * 
     * @param id the ColaEspera ID
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColaEspera(@PathVariable Long id) {
        colaesperaUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}