package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.UsuarioRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioResponse;
import edu.usco.campusbookings.application.mapper.UsuarioMapper;
import edu.usco.campusbookings.application.service.UsuarioService;
import edu.usco.campusbookings.domain.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 *   <li>UsuarioService: Service for Usuario operations.</li>
 *   <li>UsuarioMapper: Mapper for Usuario operations.</li>
 * </ul>
 * 
 * <p>Annotations:</p>
 * <ul>
 *   <li>@RestController: Designates this class as a REST controller.</li>
 *   <li>@RequestMapping: Maps HTTP requests to handler methods in this controller.</li>
 *   <li>@RequiredArgsConstructor: Generates a constructor with required arguments.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    /**
     * Creates a new Usuario.
     * 
     * @param request the Usuario request
     * @return the created Usuario response
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.createUsuario(request));
    }

    /**
     * Retrieves a Usuario by its ID.
     * 
     * @param id the Usuario ID
     * @return the found Usuario response
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    /**
     * Retrieves all usuarios.
     * 
     * @return the list of Usuario responses
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    /**
     * Updates a Usuario by its ID.
     * 
     * @param id the Usuario ID
     * @param request the Usuario request
     * @return the updated Usuario response
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioRequest request
    ) {
        return ResponseEntity.ok(usuarioService.updateUsuario(id, request));
    }

    /**
     * Deletes a Usuario by its ID.
     * 
     * @param id the Usuario ID
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}