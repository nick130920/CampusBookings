package edu.usco.campusbookings.application.dto.request;

import edu.usco.campusbookings.domain.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RolRequest {
    private Long id;
    private String nombre;
    private List<Usuario> usuarios;
}