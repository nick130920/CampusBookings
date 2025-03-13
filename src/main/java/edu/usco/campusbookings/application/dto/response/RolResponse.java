package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RolResponse {
    private Long id;
    private String nombre;
    private List<Usuario> usuarios;
}