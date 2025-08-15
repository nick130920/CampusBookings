package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.ReservaRecurrenteRequest;
import edu.usco.campusbookings.application.dto.response.ReservaRecurrenteResponse;
import edu.usco.campusbookings.domain.model.ReservaRecurrente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaRecurrenteMapper {

    private final ObjectMapper objectMapper;

    public ReservaRecurrente toEntity(ReservaRecurrenteRequest request) {
        ReservaRecurrente entity = ReservaRecurrente.builder()
                .patron(request.getPatron())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .observaciones(request.getObservaciones())
                .diaMes(request.getDiaMes())
                .intervaloRepeticion(request.getIntervaloRepeticion())
                .maxReservas(request.getMaxReservas())
                .activa(true)
                .build();

        // Convertir lista de días de semana a JSON string
        if (request.getDiasSemana() != null && !request.getDiasSemana().isEmpty()) {
            entity.setDiasSemana(convertirListaAJson(request.getDiasSemana()));
        }

        return entity;
    }

    public ReservaRecurrenteResponse toResponse(ReservaRecurrente entity) {
        ReservaRecurrenteResponse response = ReservaRecurrenteResponse.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .usuarioNombre(entity.getUsuario() != null ? 
                    entity.getUsuario().getNombre() + " " + entity.getUsuario().getApellido() : null)
                .usuarioEmail(entity.getUsuario() != null ? entity.getUsuario().getEmail() : null)
                .escenarioId(entity.getEscenario() != null ? entity.getEscenario().getId() : null)
                .escenarioNombre(entity.getEscenario() != null ? entity.getEscenario().getNombre() : null)
                .patron(entity.getPatron())
                .patronDescripcion(entity.getPatron() != null ? entity.getPatron().getDescripcion() : null)
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .observaciones(entity.getObservaciones())
                .diaMes(entity.getDiaMes())
                .intervaloRepeticion(entity.getIntervaloRepeticion())
                .activa(entity.getActiva())
                .maxReservas(entity.getMaxReservas())
                .reservasGeneradas(entity.getReservasGeneradas() != null ? entity.getReservasGeneradas().size() : 0)
                .fechaCreacion(entity.getCreatedAt())
                .fechaActualizacion(entity.getUpdatedAt())
                .creadoPor(entity.getCreatedBy())
                .actualizadoPor(entity.getUpdatedBy())
                .build();

        // Convertir JSON string a lista de días de semana
        if (entity.getDiasSemana() != null && !entity.getDiasSemana().isEmpty()) {
            response.setDiasSemana(convertirJsonALista(entity.getDiasSemana()));
        }

        // Generar descripción completa
        response.generarDescripcionCompleta();

        // Calcular próximas fechas de generación
        response.setProximasFechas(calcularProximasFechas(entity, 5));
        response.setProximaFechaGeneracion(
            response.getProximasFechas().isEmpty() ? null : response.getProximasFechas().get(0)
        );

        // Determinar si puede generar más reservas
        int reservasActuales = response.getReservasGeneradas();
        boolean dentroDelRango = LocalDate.now().isBefore(entity.getFechaFin()) || 
                                LocalDate.now().isEqual(entity.getFechaFin());
        boolean dentroDelLimite = entity.getMaxReservas() == null || reservasActuales < entity.getMaxReservas();
        response.setPuedeGenerarMas(entity.getActiva() && dentroDelRango && dentroDelLimite);

        return response;
    }

    public List<ReservaRecurrenteResponse> toResponseList(List<ReservaRecurrente> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    private String convertirListaAJson(List<Integer> lista) {
        try {
            return objectMapper.writeValueAsString(lista);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir lista a JSON: {}", e.getMessage());
            return "[]";
        }
    }

    private List<Integer> convertirJsonALista(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error al convertir JSON a lista: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<LocalDate> calcularProximasFechas(ReservaRecurrente entity, int cantidad) {
        List<LocalDate> fechas = new ArrayList<>();
        LocalDate fechaActual = LocalDate.now();
        
        // Si la fecha actual es anterior al inicio, comenzar desde fechaInicio
        if (fechaActual.isBefore(entity.getFechaInicio())) {
            fechaActual = entity.getFechaInicio();
        }

        int contador = 0;
        while (contador < cantidad && 
               (fechaActual.isBefore(entity.getFechaFin()) || fechaActual.isEqual(entity.getFechaFin()))) {
            
            if (entity.coincideConPatron(fechaActual)) {
                fechas.add(fechaActual);
                contador++;
            }
            
            fechaActual = entity.calcularProximaFecha(fechaActual);
        }

        return fechas;
    }
}
