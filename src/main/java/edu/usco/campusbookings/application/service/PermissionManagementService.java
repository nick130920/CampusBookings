package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.CreatePermissionRequest;
import edu.usco.campusbookings.application.dto.request.UpdatePermissionRequest;
import edu.usco.campusbookings.application.dto.response.PermissionResponse;
import edu.usco.campusbookings.application.exception.PermissionNotFoundException;
import edu.usco.campusbookings.application.mapper.PermissionMapper;
import edu.usco.campusbookings.application.port.input.PermissionManagementUseCase;
import edu.usco.campusbookings.application.port.output.PermissionRepositoryPort;
import edu.usco.campusbookings.domain.model.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionManagementService implements PermissionManagementUseCase {

    private final PermissionRepositoryPort permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        log.info("Creando nuevo permiso: {}", request.getName());
        
        if (permissionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un permiso con el nombre: " + request.getName());
        }

        Permission permission = permissionMapper.toEntity(request);
        Permission savedPermission = permissionRepository.save(permission);
        
        log.info("Permiso creado exitosamente con ID: {}", savedPermission.getId());
        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    public PermissionResponse updatePermission(Long permissionId, UpdatePermissionRequest request) {
        log.info("Actualizando permiso con ID: {}", permissionId);
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> PermissionNotFoundException.withId(permissionId));

        // Verificar si el nuevo nombre ya existe en otro permiso
        if (request.getName() != null && !request.getName().equals(permission.getName())) {
            if (permissionRepository.existsByName(request.getName())) {
                throw new RuntimeException("Ya existe un permiso con el nombre: " + request.getName());
            }
            permission.setName(request.getName());
        }

        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }

        if (request.getResource() != null) {
            permission.setResource(request.getResource());
        }

        if (request.getAction() != null) {
            permission.setAction(request.getAction());
        }

        Permission updatedPermission = permissionRepository.save(permission);
        log.info("Permiso actualizado exitosamente: {}", updatedPermission.getId());
        
        return permissionMapper.toResponse(updatedPermission);
    }

    @Override
    public void deletePermission(Long permissionId) {
        log.info("Eliminando permiso con ID: {}", permissionId);
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> PermissionNotFoundException.withId(permissionId));

        permissionRepository.delete(permission);
        log.info("Permiso eliminado exitosamente: {}", permissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        log.debug("Obteniendo todos los permisos");
        List<Permission> permissions = permissionRepository.findAll();
        return permissionMapper.toResponseList(permissions);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Long permissionId) {
        log.debug("Obteniendo permiso con ID: {}", permissionId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> PermissionNotFoundException.withId(permissionId));
        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByResource(String resource) {
        log.debug("Obteniendo permisos por recurso: {}", resource);
        List<Permission> permissions = permissionRepository.findByResource(resource);
        return permissionMapper.toResponseList(permissions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByAction(String action) {
        log.debug("Obteniendo permisos por acción: {}", action);
        List<Permission> permissions = permissionRepository.findByAction(action);
        return permissionMapper.toResponseList(permissions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> searchPermissions(String searchTerm) {
        log.debug("Buscando permisos con término: {}", searchTerm);
        List<Permission> permissions = permissionRepository.searchPermissions(searchTerm);
        return permissionMapper.toResponseList(permissions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableResources() {
        return Arrays.asList(
                "USUARIOS", "ROLES", "PERMISOS", "RESERVAS", "ESCENARIOS", 
                "CONFIGURACION", "REPORTES", "AUDITORIA"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableActions() {
        return Arrays.asList("CREATE", "READ", "UPDATE", "DELETE", "MANAGE");
    }
}
