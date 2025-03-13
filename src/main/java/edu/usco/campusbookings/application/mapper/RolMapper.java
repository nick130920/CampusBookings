package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.RolRequest;
import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.domain.model.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper interface for converting between Rol entities and DTOs.
 * This interface uses MapStruct for automatic implementation generation.
 * 
 * @componentModel spring - Indicates that the generated implementation should be a Spring bean.
 * 
 * Methods:
 * - toEntity(RolRequest dto): Converts a RolRequest DTO to a Rol entity.
 * - toDto(Rol rol): Converts a Rol entity to a RolResponse DTO.
 * - toDtoList(List<Rol> rol): Converts a list of Rol entities to a list of RolResponse DTOs.
 */
@Mapper(componentModel = "spring")
public interface RolMapper {

    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);

    /**
     * Converts a RolRequest DTO to a Rol entity.
     *
     * @param dto the RolRequest DTO
     * @return the converted Rol entity
     */
    Rol toEntity(RolRequest dto);

    /**
     * Converts a Rol entity to a RolResponse DTO.
     *
     * @param rol the Rol entity
     * @return the converted RolResponse DTO
     */
    RolResponse toDto(Rol rol);

    /**
     * Converts a list of Rol entities to a list of RolResponse DTOs.
     *
     * @param rol the list of Rol entities
     * @return the converted list of RolResponse DTOs
     */
    List<RolResponse> toDtoList(List<Rol> rol);
}
