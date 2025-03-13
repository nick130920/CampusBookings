package edu.usco.campusbookings.application.dto.request;

import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Rol;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioRequest {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
    private List<Reserva> reservas;
}