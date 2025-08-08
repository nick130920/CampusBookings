package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.UpdateUsuarioRolRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioDetailResponse;

import java.util.List;

public interface UserManagementUseCase {

    /**
     * Obtener todos los usuarios con información de sus roles
     */
    List<UsuarioDetailResponse> getAllUsers();

    /**
     * Obtener un usuario por ID con información de su rol
     */
    UsuarioDetailResponse getUserById(Long userId);

    /**
     * Buscar usuarios por término de búsqueda (nombre, apellido, email)
     */
    List<UsuarioDetailResponse> searchUsers(String searchTerm);

    /**
     * Cambiar el rol de un usuario
     */
    UsuarioDetailResponse updateUserRole(Long userId, UpdateUsuarioRolRequest request);

    /**
     * Obtener usuarios por rol
     */
    List<UsuarioDetailResponse> getUsersByRole(Long roleId);
}
