package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColaEsperaResponse {
    private Long id;
    private Usuario usuario;
    private Reserva reserva;
    private int posicion;
}