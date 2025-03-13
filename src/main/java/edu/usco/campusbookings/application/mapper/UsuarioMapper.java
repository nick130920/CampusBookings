package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.UsuarioRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioResponse;
import edu.usco.campusbookings.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper interface for converting between Usuario entities and DTOs.
 * This interface uses MapStruct for automatic implementation generation.
 * 
 * @componentModel spring - Indicates that the generated implementation should be a Spring bean.
 * 
 * Methods:
 * - toEntity(UsuarioRequest dto): Converts a UsuarioRequest DTO to a Usuario entity.
 * - toDto(Usuario usuario): Converts a Usuario entity to a UsuarioResponse DTO.
 * - toDtoList(List<Usuario> usuario): Converts a list of Usuario entities to a list of UsuarioResponse DTOs.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    /**
     * Converts a UsuarioRequest DTO to a Usuario entity.
     *
     * @param dto the UsuarioRequest DTO
     * @return the converted Usuario entity
     */
    Usuario toEntity(UsuarioRequest dto);

    /**
     * Converts a Usuario entity to a UsuarioResponse DTO.
     *
     * @param usuario the Usuario entity
     * @return the converted UsuarioResponse DTO
     */
    UsuarioResponse toDto(Usuario usuario);

    /**
     * Converts a list of Usuario entities to a list of UsuarioResponse DTOs.
     *
     * @param usuario the list of Usuario entities
     * @return the converted list of UsuarioResponse DTOs
     */
    List<UsuarioResponse> toDtoList(List<Usuario> usuario);
}
