package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.response.HistorialReservasResponse;
import edu.usco.campusbookings.domain.model.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistorialReservasMapper {

    @Mapping(source = "escenario.nombre", target = "escenarioNombre")
    @Mapping(source = "escenario.tipo", target = "tipo")
    @Mapping(source = "estado.nombre", target = "estado")
    HistorialReservasResponse toResponse(Reserva reserva);
}
