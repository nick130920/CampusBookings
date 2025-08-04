package edu.usco.campusbookings.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;
import edu.usco.campusbookings.domain.model.Reserva;

/**
 * Mapper for converting between Reserva entities and DTOs.
 */
@Mapper(componentModel = "spring", uses = {EscenarioMapper.class})
public interface ReservaMapper {
    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "escenario", ignore = true)
    Reserva toEntity(ReservaRequest request);

    @Mapping(source = "escenario.id", target = "escenarioId")
    @Mapping(source = "escenario.nombre", target = "escenarioNombre")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    @Mapping(source = "estado", target = "estadoId", qualifiedByName = "mapEstadoId")
    @Mapping(source = "estado", target = "estadoNombre", qualifiedByName = "mapEstadoNombre")
    @Mapping(source = "fechaInicio", target = "fechaInicio")
    @Mapping(source = "fechaFin", target = "fechaFin")
    @Mapping(source = "createdDate", target = "fechaCreacion")
    @Mapping(source = "modifiedDate", target = "fechaActualizacion")
    ReservaResponse toDto(Reserva reserva);
    
    @Named("mapEstadoId")
    default Long mapEstadoId(edu.usco.campusbookings.domain.model.EstadoReserva estado) {
        return estado != null ? estado.getId() : null;
    }
    
    @Named("mapEstadoNombre")
    default String mapEstadoNombre(edu.usco.campusbookings.domain.model.EstadoReserva estado) {
        return estado != null ? estado.getNombre() : null;
    }
}
