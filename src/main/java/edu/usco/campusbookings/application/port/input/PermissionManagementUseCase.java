package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.CreatePermissionRequest;
import edu.usco.campusbookings.application.dto.request.UpdatePermissionRequest;
import edu.usco.campusbookings.application.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionManagementUseCase {

    /**
     * Crear un nuevo permiso
     */
    PermissionResponse createPermission(CreatePermissionRequest request);

    /**
     * Actualizar un permiso existente
     */
    PermissionResponse updatePermission(Long permissionId, UpdatePermissionRequest request);

    /**
     * Eliminar un permiso
     */
    void deletePermission(Long permissionId);

    /**
     * Obtener todos los permisos
     */
    List<PermissionResponse> getAllPermissions();

    /**
     * Obtener un permiso por ID
     */
    PermissionResponse getPermissionById(Long permissionId);

    /**
     * Obtener permisos por recurso
     */
    List<PermissionResponse> getPermissionsByResource(String resource);

    /**
     * Obtener permisos por acción
     */
    List<PermissionResponse> getPermissionsByAction(String action);

    /**
     * Buscar permisos por término de búsqueda
     */
    List<PermissionResponse> searchPermissions(String searchTerm);

    /**
     * Obtener recursos disponibles
     */
    List<String> getAvailableResources();

    /**
     * Obtener acciones disponibles
     */
    List<String> getAvailableActions();
}
