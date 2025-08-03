package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.UsuarioRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioResponse;
import edu.usco.campusbookings.domain.model.Usuario;
import org.mapstruct.Mapper;


/**
 * Mapper interface for converting between Usuario entities and DTOs.
 * This interface uses MapStruct for automatic implementation generation.
 * 
 * @componentModel spring - Indicates that the generated implementation should be a Spring bean.
 * 
 * Methods:
 * - toDomain(UsuarioRequest request): Converts a UsuarioRequest DTO to a Usuario entity.
 * - toResponse(Usuario usuario): Converts a Usuario entity to a UsuarioResponse DTO.
 * - updateDomain(Usuario usuario, UsuarioRequest request): Updates an existing Usuario entity with the data from a UsuarioRequest DTO.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    /**
     * Converts a UsuarioRequest DTO to a Usuario entity.
     *
     * @param request the UsuarioRequest DTO
     * @return the converted Usuario entity
     */
    Usuario toDomain(UsuarioRequest request);

    /**
     * Converts a Usuario entity to a UsuarioResponse DTO.
     *
     * @param usuario the Usuario entity
     * @return the converted UsuarioResponse DTO
     */
    UsuarioResponse toResponse(Usuario usuario);

}
