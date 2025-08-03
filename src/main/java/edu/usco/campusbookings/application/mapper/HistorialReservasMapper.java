package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.response.HistorialReservasResponse;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface HistorialReservasMapper {

    @Mapping(source = "escenario.nombre", target = "escenarioNombre")
    @Mapping(source = "escenario.tipo", target = "tipo", qualifiedByName = "mapTipoEscenarioToString")
    @Mapping(source = "estado.nombre", target = "estado")
    HistorialReservasResponse toResponse(Reserva reserva);

    @Named("mapTipoEscenarioToString")
    default String mapTipoEscenarioToString(TipoEscenario tipo) {
        return tipo != null ? tipo.getNombre() : null;
    }
}
