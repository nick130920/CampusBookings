package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.HistorialReservasRequest;
import edu.usco.campusbookings.application.dto.response.HistorialReservasResponse;
import edu.usco.campusbookings.application.port.input.HistorialReservasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas/historial")
@RequiredArgsConstructor
public class HistorialReservasController {

    private final HistorialReservasUseCase historialReservasUseCase;

    @GetMapping
    public ResponseEntity<List<HistorialReservasResponse>> consultarHistorial(
            @RequestBody HistorialReservasRequest request) {
        List<HistorialReservasResponse> historial = historialReservasUseCase.consultarHistorial(request);
        return ResponseEntity.ok(historial);
    }
}
