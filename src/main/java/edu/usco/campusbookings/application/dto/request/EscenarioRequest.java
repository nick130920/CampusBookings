package edu.usco.campusbookings.application.dto.request;

import java.util.List;

import edu.usco.campusbookings.domain.model.HorarioDisponible;
import edu.usco.campusbookings.domain.model.Reserva;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EscenarioRequest {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "Debe especificar al menos un horario disponible")
    @NotEmpty(message = "Debe especificar al menos un horario disponible")
    private List<HorarioDisponible> horariosDisponibles;

    @NotNull(message = "Debe especificar al menos un recurso")
    @NotEmpty(message = "Debe especificar al menos un recurso")
    private List<Reserva> reservas;
}