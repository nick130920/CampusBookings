package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.request.CreateRolRequest;
import edu.usco.campusbookings.application.dto.response.RolDetailResponse;
import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.application.port.output.RolRepositoryPort;
import edu.usco.campusbookings.domain.model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolMapper {

    private final PermissionMapper permissionMapper;
    private final RolRepositoryPort rolRepository;

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
                .usuariosCount(safeGetUsuariosCount(rol))
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
                .usuariosCount(safeGetUsuariosCount(rol))
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

    /**
     * Método seguro para obtener el conteo de usuarios sin cargar la colección completa.
     * Evita StackOverflowError por referencias circulares usando consulta específica.
     */
    private int safeGetUsuariosCount(Rol rol) {
        try {
            // Usar consulta específica para obtener el conteo sin cargar entidades
            return (int) rolRepository.countUsuariosByRolId(rol.getId());
        } catch (Exception e) {
            // En caso de cualquier error, retornar 0
            return 0;
        }
    }
}