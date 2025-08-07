package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.CreateRolRequest;
import edu.usco.campusbookings.application.dto.request.UpdateRolRequest;
import edu.usco.campusbookings.application.dto.response.RolDetailResponse;
import edu.usco.campusbookings.application.dto.response.RolResponse;

import java.util.List;

public interface RoleManagementUseCase {

    /**
     * Crear un nuevo rol con permisos
     */
    RolDetailResponse createRole(CreateRolRequest request);

    /**
     * Actualizar un rol existente
     */
    RolDetailResponse updateRole(Long roleId, UpdateRolRequest request);

    /**
     * Eliminar un rol
     */
    void deleteRole(Long roleId);

    /**
     * Obtener todos los roles
     */
    List<RolResponse> getAllRoles();

    /**
     * Obtener roles activos únicamente
     */
    List<RolResponse> getActiveRoles();

    /**
     * Obtener un rol por ID con sus permisos
     */
    RolDetailResponse getRoleById(Long roleId);

    /**
     * Obtener un rol por nombre con sus permisos
     */
    RolDetailResponse getRoleByName(String roleName);

    /**
     * Buscar roles por término de búsqueda
     */
    List<RolResponse> searchRoles(String searchTerm);

    /**
     * Asignar permisos a un rol
     */
    RolDetailResponse assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * Remover permisos de un rol
     */
    RolDetailResponse removePermissionsFromRole(Long roleId, List<Long> permissionIds);

    /**
     * Activar/desactivar un rol
     */
    RolResponse toggleRoleStatus(Long roleId);
}
