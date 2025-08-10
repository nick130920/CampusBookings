package edu.usco.campusbookings.application.dto.request;

import edu.usco.campusbookings.domain.model.AlertaReserva;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurarAlertaRequest {

    @NotNull(message = "Los tipos de alerta son obligatorios")
    private List<AlertaReserva.TipoAlerta> tiposAlerta;

    @NotNull(message = "Los canales de envío son obligatorios")
    private List<String> canalesEnvio; // EMAIL, WEBSOCKET, PUSH

    private String mensajePersonalizado;
    private Boolean habilitarRecordatorios;
    private Integer horasAnticipacion24h;
    private Integer horasAnticipacion2h;
    private Integer minutosAnticipacion30min;

    // Configuraciones específicas por tipo de escenario
    private Long tipoEscenarioId;
    private Boolean aplicarATodosLosEscenarios;
}
