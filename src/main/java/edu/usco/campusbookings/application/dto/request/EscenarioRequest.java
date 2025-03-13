package edu.usco.campusbookings.application.dto.request;

import edu.usco.campusbookings.domain.model.HorarioDisponible;
import edu.usco.campusbookings.domain.model.Reserva;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EscenarioRequest {
    private Long id;
    private String nombre;
    private String tipo;
    private String ubicacion;
    private List<HorarioDisponible> horariosDisponibles;
    private List<Reserva> reservas;
}