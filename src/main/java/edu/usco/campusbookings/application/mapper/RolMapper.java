package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.CreateRolRequest;
import edu.usco.campusbookings.application.dto.response.RolDetailResponse;
import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.domain.model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolMapper {

    private final PermissionMapper permissionMapper;

    public Rol toEntity(CreateRolRequest request) {
        return Rol.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();
    }

    public RolResponse toResponse(Rol rol) {
        return RolResponse.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.getActivo())
                .usuariosCount(rol.getUsuarios() != null ? rol.getUsuarios().size() : 0)
                .permissionsCount(rol.getPermissions() != null ? rol.getPermissions().size() : 0)
                .createdAt(rol.getCreatedDate())
                .updatedAt(rol.getModifiedDate())
                .build();
    }

    public RolDetailResponse toDetailResponse(Rol rol) {
        return RolDetailResponse.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.getActivo())
                .usuariosCount(rol.getUsuarios() != null ? rol.getUsuarios().size() : 0)
                .permissions(rol.getPermissions() != null ? 
                    permissionMapper.toResponseList(rol.getPermissions().stream().collect(Collectors.toList())) : 
                    List.of())
                .createdAt(rol.getCreatedDate())
                .updatedAt(rol.getModifiedDate())
                .build();
    }

    public List<RolResponse> toResponseList(List<Rol> roles) {
        return roles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}