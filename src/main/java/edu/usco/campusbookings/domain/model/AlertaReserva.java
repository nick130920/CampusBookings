package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "alertas_reservas")
public class AlertaReserva extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La reserva es obligatoria")
    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @NotNull(message = "El tipo de alerta es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo;

    @NotNull(message = "La fecha de envío es obligatoria")
    private LocalDateTime fechaEnvio;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoAlerta estado;

    private String mensaje;
    private String canalEnvio; // EMAIL, WEBSOCKET, PUSH
    private LocalDateTime fechaEnviado;
    private String detallesEnvio; // JSON con información adicional del envío
    private Integer intentosEnvio;
    private String motivoFallo;

    public enum TipoAlerta {
        RECORDATORIO_24H("Recordatorio 24 horas antes"),
        RECORDATORIO_2H("Recordatorio 2 horas antes"),
        RECORDATORIO_30MIN("Recordatorio 30 minutos antes"),
        CONFIRMACION_LLEGADA("Confirmación de llegada"),
        EXPIRACION_RESERVA("Expiración de reserva"),
        CAMBIO_ESTADO("Cambio de estado"),
        CANCELACION_AUTOMATICA("Cancelación automática");

        private final String descripcion;

        TipoAlerta(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public enum EstadoAlerta {
        PENDIENTE("Pendiente de envío"),
        ENVIADO("Enviado exitosamente"),
        FALLIDO("Falló el envío"),
        CANCELADO("Cancelado"),
        PROGRAMADO("Programado para envío");

        private final String descripcion;

        EstadoAlerta(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Verifica si la alerta puede ser enviada
     */
    public boolean puedeSerEnviada() {
        return estado == EstadoAlerta.PENDIENTE || estado == EstadoAlerta.PROGRAMADO;
    }

    /**
     * Marca la alerta como enviada exitosamente
     */
    public void marcarComoEnviada() {
        this.estado = EstadoAlerta.ENVIADO;
        this.fechaEnviado = LocalDateTime.now();
    }

    /**
     * Marca la alerta como fallida
     */
    public void marcarComoFallida(String motivo) {
        this.estado = EstadoAlerta.FALLIDO;
        this.motivoFallo = motivo;
        this.intentosEnvio = (this.intentosEnvio != null) ? this.intentosEnvio + 1 : 1;
    }

    /**
     * Verifica si la alerta está vencida (pasó su fecha de envío)
     */
    public boolean estaVencida() {
        return LocalDateTime.now().isAfter(fechaEnvio);
    }
}
