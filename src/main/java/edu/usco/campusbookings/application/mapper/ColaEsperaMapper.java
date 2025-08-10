package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.ColaEsperaRequest;
import edu.usco.campusbookings.application.dto.response.ColaEsperaResponse;
import edu.usco.campusbookings.domain.model.ColaEspera;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between ColaEspera entities and DTOs.
 * This interface uses MapStruct for automatic implementation generation.
 * 
 * @componentModel spring - Indicates that the generated implementation should be a Spring bean.
 * 
 * Methods:
 * - toEntity(ColaEsperaRequest dto): Converts a ColaEsperaRequest DTO to a ColaEspera entity.
 * - toDto(ColaEspera colaespera): Converts a ColaEspera entity to a ColaEsperaResponse DTO.
 * - toDtoList(List<ColaEspera> colaespera): Converts a list of ColaEspera entities to a list of ColaEsperaResponse DTOs.
 */
@Mapper(componentModel = "spring")
public interface ColaEsperaMapper {

    /**
     * Converts a ColaEsperaRequest DTO to a ColaEspera entity.
     *
     * @param dto the ColaEsperaRequest DTO
     * @return the converted ColaEspera entity
     */
    ColaEspera toEntity(ColaEsperaRequest dto);

    /**
     * Converts a ColaEspera entity to a ColaEsperaResponse DTO.
     *
     * @param colaespera the ColaEspera entity
     * @return the converted ColaEsperaResponse DTO
     */
    ColaEsperaResponse toDto(ColaEspera colaespera);

    /**
     * Converts a list of ColaEspera entities to a list of ColaEsperaResponse DTOs.
     *
     * @param colaespera the list of ColaEspera entities
     * @return the converted list of ColaEsperaResponse DTOs
     */
    List<ColaEsperaResponse> toDtoList(List<ColaEspera> colaespera);
}
