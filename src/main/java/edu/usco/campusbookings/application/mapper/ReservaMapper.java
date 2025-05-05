package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservaMapper {
    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Reserva toEntity(ReservaRequest request);

    ReservaResponse toDto(Reserva reserva);
}
