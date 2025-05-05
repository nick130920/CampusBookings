package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.EscenarioRequest;
import edu.usco.campusbookings.application.dto.response.DetalleEscenarioResponse;
import edu.usco.campusbookings.application.dto.response.EscenarioResponse;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.HorarioDisponible;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper interface for converting between Escenario entities and DTOs.
 * This interface uses MapStruct for automatic implementation generation.
 * 
 * @componentModel spring - Indicates that the generated implementation should be a Spring bean.
 * 
 * Methods:
 * - toEntity(EscenarioRequest dto): Converts a EscenarioRequest DTO to a Escenario entity.
 * - toDto(Escenario escenario): Converts a Escenario entity to a EscenarioResponse DTO.
 * - toDtoList(List<Escenario> escenario): Converts a list of Escenario entities to a list of EscenarioResponse DTOs.
 */
@Mapper(componentModel = "spring")
public interface EscenarioMapper {

    EscenarioMapper INSTANCE = Mappers.getMapper(EscenarioMapper.class);

    /**
     * Converts a EscenarioRequest DTO to a Escenario entity.
     *
     * @param dto the EscenarioRequest DTO
     * @return the converted Escenario entity
     */
    Escenario toEntity(EscenarioRequest dto);

    /**
     * Converts a Escenario entity to a EscenarioResponse DTO.
     *
     * @param escenario the Escenario entity
     * @return the converted EscenarioResponse DTO
     */
    EscenarioResponse toDto(Escenario escenario);

    /**
     * Converts a list of Escenario entities to a list of EscenarioResponse DTOs.
     *
     * @param escenario the list of Escenario entities
     * @return the converted list of EscenarioResponse DTOs
     */
    List<EscenarioResponse> toDtoList(List<Escenario> escenario);

    @Mapping(source = "horariosDisponibles", target = "horariosDisponibles", qualifiedByName = "toHorariosString")
    @Mapping(source = "capacidad", target = "capacidad")
    @Mapping(source = "descripcion", target = "descripcion")
    @Mapping(source = "imagenUrl", target = "imagenUrl")
    @Mapping(source = "recursos", target = "recursos")
    DetalleEscenarioResponse toDetalleResponse(Escenario escenario);

    @Named("toHorariosString")
    default List<String> toHorariosString(List<HorarioDisponible> horarios) {
        return horarios.stream()
            .map(horario -> horario.getHoraInicio() + " - " + horario.getHoraFin())
            .toList();
    }
}
