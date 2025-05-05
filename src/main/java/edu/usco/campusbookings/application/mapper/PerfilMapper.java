package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.response.PerfilResponse;
import edu.usco.campusbookings.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerfilMapper {

    @Mapping(source = "rol.nombre", target = "rol")
    PerfilResponse toResponse(Usuario usuario);
}
