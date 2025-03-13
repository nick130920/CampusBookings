package edu.usco.campusbookings.application.dto.request;

import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColaEsperaRequest {
    private Long id;
    private Usuario usuario;
    private Reserva reserva;
    private int posicion;
}