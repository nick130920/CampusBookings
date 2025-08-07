package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.CreateRolRequest;
import edu.usco.campusbookings.application.dto.request.UpdateRolRequest;
import edu.usco.campusbookings.application.dto.response.RolDetailResponse;
import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.application.exception.PermissionNotFoundException;
import edu.usco.campusbookings.application.exception.RolNotFoundException;
import edu.usco.campusbookings.application.exception.RolValidationException;
import edu.usco.campusbookings.application.mapper.RolMapper;
import edu.usco.campusbookings.application.port.input.RoleManagementUseCase;
import edu.usco.campusbookings.application.port.output.PermissionRepositoryPort;
import edu.usco.campusbookings.application.port.output.RolRepositoryPort;
import edu.usco.campusbookings.domain.model.Permission;
import edu.usco.campusbookings.domain.model.Rol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleManagementService implements RoleManagementUseCase {

    private final RolRepositoryPort rolRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RolMapper rolMapper;

    @Override
    public RolDetailResponse createRole(CreateRolRequest request) {
        log.info("Creando nuevo rol: {}", request.getNombre());
        
        if (rolRepository.existsByNombre(request.getNombre())) {
            throw RolValidationException.duplicateName(request.getNombre());
        }

        Rol rol = rolMapper.toEntity(request);
        
        // Asignar permisos si se proporcionaron
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = getPermissionsByIds(request.getPermissionIds());
            rol.setPermissions(permissions);
        }

        Rol savedRol = rolRepository.save(rol);
        log.info("Rol creado exitosamente con ID: {}", savedRol.getId());
        
        // Cargar el rol con permisos para la respuesta
        Rol rolWithPermissions = rolRepository.findByIdWithPermissions(savedRol.getId())
                .orElse(savedRol);
        
        return rolMapper.toDetailResponse(rolWithPermissions);
    }

    @Override
    public RolDetailResponse updateRole(Long roleId, UpdateRolRequest request) {
        log.info("Actualizando rol con ID: {}", roleId);
        
        Rol rol = rolRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> RolNotFoundException.withId(roleId));

        // Verificar si el nuevo nombre ya existe en otro rol
        if (request.getNombre() != null && !request.getNombre().equals(rol.getNombre())) {
            if (rolRepository.existsByNombre(request.getNombre())) {
                throw RolValidationException.duplicateName(request.getNombre());
            }
            rol.setNombre(request.getNombre());
        }

        if (request.getDescripcion() != null) {
            rol.setDescripcion(request.getDescripcion());
        }

        // Actualizar estado activo con validaciones
        if (request.getActivo() != null && !request.getActivo().equals(rol.getActivo())) {
            if (!request.getActivo() && rol.getUsuarios() != null && !rol.getUsuarios().isEmpty()) {
                throw RolValidationException.cannotDeactivateRoleWithUsers(rol.getNombre());
            }
            rol.setActivo(request.getActivo());
        }

        // Actualizar permisos si se proporcionaron
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = getPermissionsByIds(request.getPermissionIds());
            rol.setPermissions(permissions);
        }

        Rol updatedRol = rolRepository.save(rol);
        log.info("Rol actualizado exitosamente: {}", updatedRol.getId());
        
        return rolMapper.toDetailResponse(updatedRol);
    }

    @Override
    public void deleteRole(Long roleId) {
        log.info("Eliminando rol con ID: {}", roleId);
        
        Rol rol = rolRepository.findById(roleId)
                .orElseThrow(() -> RolNotFoundException.withId(roleId));

        // Verificar que el rol no tenga usuarios asignados
        if (rol.getUsuarios() != null && !rol.getUsuarios().isEmpty()) {
            throw RolValidationException.cannotDeleteRoleWithUsers(rol.getNombre(), rol.getUsuarios().size());
        }

        rolRepository.delete(rol);
        log.info("Rol eliminado exitosamente: {}", roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponse> getAllRoles() {
        log.debug("Obteniendo todos los roles");
        List<Rol> roles = rolRepository.findAll();
        return rolMapper.toResponseList(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponse> getActiveRoles() {
        log.debug("Obteniendo roles activos");
        List<Rol> roles = rolRepository.findByActivoTrue();
        return rolMapper.toResponseList(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public RolDetailResponse getRoleById(Long roleId) {
        log.debug("Obteniendo rol con ID: {}", roleId);
        Rol rol = rolRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> RolNotFoundException.withId(roleId));
        return rolMapper.toDetailResponse(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolDetailResponse getRoleByName(String roleName) {
        log.debug("Obteniendo rol con nombre: {}", roleName);
        Rol rol = rolRepository.findByNombreWithPermissions(roleName)
                .orElseThrow(() -> RolNotFoundException.withName(roleName));
        return rolMapper.toDetailResponse(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponse> searchRoles(String searchTerm) {
        log.debug("Buscando roles con término: {}", searchTerm);
        List<Rol> roles = rolRepository.searchRoles(searchTerm);
        return rolMapper.toResponseList(roles);
    }

    @Override
    public RolDetailResponse assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        log.info("Asignando permisos al rol con ID: {}", roleId);
        
        Rol rol = rolRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> RolNotFoundException.withId(roleId));

        Set<Permission> newPermissions = getPermissionsByIds(permissionIds);
        
        // Agregar nuevos permisos a los existentes
        if (rol.getPermissions() == null) {
            rol.setPermissions(new HashSet<>());
        }
        rol.getPermissions().addAll(newPermissions);

        Rol updatedRol = rolRepository.save(rol);
        log.info("Permisos asignados exitosamente al rol: {}", roleId);
        
        return rolMapper.toDetailResponse(updatedRol);
    }

    @Override
    public RolDetailResponse removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        log.info("Removiendo permisos del rol con ID: {}", roleId);
        
        Rol rol = rolRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> RolNotFoundException.withId(roleId));

        Set<Permission> permissionsToRemove = getPermissionsByIds(permissionIds);
        
        if (rol.getPermissions() != null) {
            rol.getPermissions().removeAll(permissionsToRemove);
        }

        Rol updatedRol = rolRepository.save(rol);
        log.info("Permisos removidos exitosamente del rol: {}", roleId);
        
        return rolMapper.toDetailResponse(updatedRol);
    }

    @Override
    public RolResponse toggleRoleStatus(Long roleId) {
        log.info("Cambiando estado del rol con ID: {}", roleId);
        
        Rol rol = rolRepository.findById(roleId)
                .orElseThrow(() -> RolNotFoundException.withId(roleId));

        // Si se va a desactivar, verificar que no tenga usuarios
        if (rol.getActivo() && rol.getUsuarios() != null && !rol.getUsuarios().isEmpty()) {
            throw RolValidationException.cannotDeactivateRoleWithUsers(rol.getNombre());
        }

        rol.setActivo(!rol.getActivo());
        Rol updatedRol = rolRepository.save(rol);
        
        log.info("Estado del rol cambiado a: {}", updatedRol.getActivo());
        return rolMapper.toResponse(updatedRol);
    }

    private Set<Permission> getPermissionsByIds(List<Long> permissionIds) {
        return permissionIds.stream()
                .map(id -> permissionRepository.findById(id)
                        .orElseThrow(() -> PermissionNotFoundException.withId(id)))
                .collect(Collectors.toSet());
    }
}
