package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.response.ScenarioTypePermissionResponse;
import edu.usco.campusbookings.domain.model.ScenarioTypePermission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioTypePermissionMapper {

    public ScenarioTypePermissionResponse toResponse(ScenarioTypePermission permission) {
        if (permission == null) {
            return null;
        }

        return ScenarioTypePermissionResponse.builder()
                .id(permission.getId())
                .usuario(mapUsuario(permission))
                .tipoEscenario(mapTipoEscenario(permission))
                .action(permission.getAction())
                .active(permission.getActive() != null ? permission.getActive() : true)
                .createdDate(permission.getCreatedDate())
                .modifiedDate(permission.getModifiedDate())
                .build();
    }

    public List<ScenarioTypePermissionResponse> toResponseList(List<ScenarioTypePermission> permissions) {
        if (permissions == null) {
            return List.of();
        }
        
        return permissions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ScenarioTypePermissionResponse.UsuarioSummary mapUsuario(ScenarioTypePermission permission) {
        if (permission.getUsuario() == null) {
            return null;
        }

        return ScenarioTypePermissionResponse.UsuarioSummary.builder()
                .id(permission.getUsuario().getId())
                .email(permission.getUsuario().getEmail())
                .nombre(permission.getUsuario().getNombre())
                .apellido(permission.getUsuario().getApellido())
                .build();
    }

    private ScenarioTypePermissionResponse.TipoEscenarioSummary mapTipoEscenario(ScenarioTypePermission permission) {
        if (permission.getTipoEscenario() == null) {
            return null;
        }

        return ScenarioTypePermissionResponse.TipoEscenarioSummary.builder()
                .id(permission.getTipoEscenario().getId())
                .nombre(permission.getTipoEscenario().getNombre())
                .descripcion(permission.getTipoEscenario().getDescripcion())
                .build();
    }
}
