package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.EscenarioRequest;
import edu.usco.campusbookings.application.dto.response.DetalleEscenarioResponse;
import edu.usco.campusbookings.application.dto.response.EscenarioResponse;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.HorarioDisponible;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.domain.model.Ubicacion;
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
     * Converts a string representation of a scenario type to a TipoEscenario entity.
     * If the input is null or empty, returns null.
     *
     * @param tipo the scenario type name
     * @return the corresponding TipoEscenario entity
     */
    @Named("mapToTipoEscenario")
    default TipoEscenario mapToTipoEscenario(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return null;
        }
        return TipoEscenario.builder()
                .nombre(tipo.trim())
                .activo(true)
                .build();
    }
    
    /**
     * Converts a TipoEscenario entity to its string representation.
     * If the input is null, returns null.
     *
     * @param tipoEscenario the TipoEscenario entity
     * @return the scenario type name
     */
    default String mapTipoEscenarioToString(TipoEscenario tipoEscenario) {
        return tipoEscenario != null ? tipoEscenario.getNombre() : null;
    }
    
    /**
     * Converts a string representation of a location to a Ubicacion entity.
     * If the input is null or empty, returns null.
     *
     * @param ubicacion the location name
     * @return the corresponding Ubicacion entity
     */
    @Named("mapToUbicacion")
    default Ubicacion mapToUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            return null;
        }
        return Ubicacion.builder()
                .nombre(ubicacion.trim())
                .activo(true)
                .build();
    }
    
    /**
     * Converts a Ubicacion entity to its string representation.
     * If the input is null, returns null.
     *
     * @param ubicacion the Ubicacion entity
     * @return the location name
     */
    default String mapUbicacionToString(Ubicacion ubicacion) {
        return ubicacion != null ? ubicacion.getNombre() : null;
    }

    /**
     * Converts a EscenarioRequest DTO to a Escenario entity.
     *
     * @param dto the EscenarioRequest DTO
     * @return the converted Escenario entity
     */
    @Mapping(source = "tipo", target = "tipo", qualifiedByName = "mapToTipoEscenario")
    @Mapping(source = "ubicacion", target = "ubicacion", qualifiedByName = "mapToUbicacion")
    @Mapping(target = "horariosDisponibles", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "id", ignore = true)
    Escenario toEntity(EscenarioRequest dto);

    /**
     * Converts a Escenario entity to a EscenarioResponse DTO.
     *
     * @param escenario the Escenario entity
     * @return the converted EscenarioResponse DTO
     */
    @Mapping(source = "tipo.nombre", target = "tipo")
    @Mapping(source = "ubicacion.nombre", target = "ubicacion")
    @Mapping(target = "caracteristicas", ignore = true)
    @Mapping(target = "horarioApertura", ignore = true)
    @Mapping(target = "horarioCierre", ignore = true)
    @Mapping(target = "costoPorHora", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "actualizadoPor", ignore = true)
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
    @Mapping(source = "tipo.nombre", target = "tipo")
    @Mapping(source = "ubicacion.nombre", target = "ubicacion")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    DetalleEscenarioResponse toDetalleResponse(Escenario escenario);

    @Named("toHorariosString")
    default List<String> toHorariosString(List<HorarioDisponible> horarios) {
        return horarios.stream()
            .map(horario -> horario.getHoraInicio() + " - " + horario.getHoraFin())
            .toList();
    }
}
