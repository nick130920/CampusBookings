package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.response.AlertaReservaResponse;
import edu.usco.campusbookings.domain.model.AlertaReserva;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertaReservaMapper {

    public AlertaReservaResponse toDto(AlertaReserva alertaReserva) {
        if (alertaReserva == null) {
            return null;
        }

        return AlertaReservaResponse.builder()
                .id(alertaReserva.getId())
                .reservaId(alertaReserva.getReserva().getId())
                .reservaEscenario(alertaReserva.getReserva().getEscenario().getNombre())
                .reservaUsuario(alertaReserva.getReserva().getUsuario().getNombre() + " " + 
                              alertaReserva.getReserva().getUsuario().getApellido())
                .reservaFechaInicio(alertaReserva.getReserva().getFechaInicio())
                .reservaFechaFin(alertaReserva.getReserva().getFechaFin())
                .tipo(alertaReserva.getTipo())
                .tipoDescripcion(alertaReserva.getTipo().getDescripcion())
                .fechaEnvio(alertaReserva.getFechaEnvio())
                .estado(alertaReserva.getEstado())
                .estadoDescripcion(alertaReserva.getEstado().getDescripcion())
                .mensaje(alertaReserva.getMensaje())
                .canalEnvio(alertaReserva.getCanalEnvio())
                .fechaEnviado(alertaReserva.getFechaEnviado())
                .intentosEnvio(alertaReserva.getIntentosEnvio())
                .motivoFallo(alertaReserva.getMotivoFallo())
                .fechaCreacion(alertaReserva.getCreatedDate())
                .fechaModificacion(alertaReserva.getModifiedDate())
                .build();
    }

    public List<AlertaReservaResponse> toDtoList(List<AlertaReserva> alertasReserva) {
        if (alertasReserva == null) {
            return null;
        }

        return alertasReserva.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Mapea una alerta básica sin todos los detalles de reserva (para listados)
     */
    public AlertaReservaResponse toDtoBasico(AlertaReserva alertaReserva) {
        if (alertaReserva == null) {
            return null;
        }

        return AlertaReservaResponse.builder()
                .id(alertaReserva.getId())
                .reservaId(alertaReserva.getReserva().getId())
                .tipo(alertaReserva.getTipo())
                .tipoDescripcion(alertaReserva.getTipo().getDescripcion())
                .fechaEnvio(alertaReserva.getFechaEnvio())
                .estado(alertaReserva.getEstado())
                .estadoDescripcion(alertaReserva.getEstado().getDescripcion())
                .mensaje(alertaReserva.getMensaje())
                .canalEnvio(alertaReserva.getCanalEnvio())
                .fechaEnviado(alertaReserva.getFechaEnviado())
                .intentosEnvio(alertaReserva.getIntentosEnvio())
                .build();
    }
}
