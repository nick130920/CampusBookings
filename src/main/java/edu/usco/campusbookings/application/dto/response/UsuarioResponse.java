package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Rol;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Rol rol;
    private List<Reserva> reservas;
}