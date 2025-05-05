package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.BuscarEscenariosRequest;
import edu.usco.campusbookings.application.dto.response.EscenarioResponse;
import edu.usco.campusbookings.application.port.input.EscenarioUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/escenarios")
public class BuscarEscenariosController {

    private final EscenarioUseCase escenarioUseCase;

    public BuscarEscenariosController(EscenarioUseCase escenarioUseCase) {
        this.escenarioUseCase = escenarioUseCase;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EscenarioResponse>> buscarEscenarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String tipo
    ) {
        BuscarEscenariosRequest request = new BuscarEscenariosRequest();
        request.setNombre(nombre);
        request.setUbicacion(ubicacion);
        request.setTipo(tipo);
        return ResponseEntity.ok(escenarioUseCase.buscarEscenarios(request));
    }
}
