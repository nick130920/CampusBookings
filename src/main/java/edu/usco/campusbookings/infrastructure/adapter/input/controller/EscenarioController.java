package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.EscenarioRequest;
import edu.usco.campusbookings.application.dto.response.EscenarioResponse;
import edu.usco.campusbookings.application.port.input.EscenarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escenarios")
@RequiredArgsConstructor
@Tag(name = "Escenarios", description = "Operaciones relacionadas con la gestión de escenarios")
public class EscenarioController {

    private final EscenarioUseCase escenarioUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Registrar un nuevo escenario")
    public ResponseEntity<EscenarioResponse> registrarEscenario(
            @Valid @RequestBody EscenarioRequest request
    ) {
        return ResponseEntity.ok(escenarioUseCase.createEscenario(request));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los escenarios")
    public ResponseEntity<List<EscenarioResponse>> obtenerEscenarios() {
        return ResponseEntity.ok(escenarioUseCase.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un escenario por ID")
    public ResponseEntity<EscenarioResponse> obtenerEscenarioPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(escenarioUseCase.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Actualizar un escenario")
    public ResponseEntity<EscenarioResponse> actualizarEscenario(
            @PathVariable Long id,
            @Valid @RequestBody EscenarioRequest request
    ) {
        return ResponseEntity.ok(escenarioUseCase.updateEscenario(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Eliminar un escenario")
    public ResponseEntity<Void> eliminarEscenario(
            @PathVariable Long id
    ) {
        escenarioUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
