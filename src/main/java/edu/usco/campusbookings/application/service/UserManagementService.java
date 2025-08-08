package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.UpdateUsuarioRolRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioDetailResponse;
import edu.usco.campusbookings.application.exception.UsuarioNotFoundException;
import edu.usco.campusbookings.application.exception.RolNotFoundException;

import edu.usco.campusbookings.application.port.input.UserManagementUseCase;
import edu.usco.campusbookings.application.port.output.RolRepositoryPort;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserManagementService implements UserManagementUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDetailResponse> getAllUsers() {
        log.debug("Obteniendo todos los usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::mapToUsuarioDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDetailResponse getUserById(Long userId) {
        log.debug("Obteniendo usuario con ID: {}", userId);
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> UsuarioNotFoundException.withId(userId));
        return mapToUsuarioDetailResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDetailResponse> searchUsers(String searchTerm) {
        log.debug("Buscando usuarios con término: {}", searchTerm);
        List<Usuario> usuarios = usuarioRepository.searchUsuarios(searchTerm);
        return usuarios.stream()
                .map(this::mapToUsuarioDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDetailResponse updateUserRole(Long userId, UpdateUsuarioRolRequest request) {
        log.info("Actualizando rol del usuario con ID: {} al rol ID: {}", userId, request.getRolId());
        
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> UsuarioNotFoundException.withId(userId));
        
        Rol nuevoRol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> RolNotFoundException.withId(request.getRolId()));
        
        // Validar que el rol esté activo
        if (!nuevoRol.getActivo()) {
            throw new IllegalArgumentException("No se puede asignar un rol inactivo");
        }
        
        String rolAnterior = usuario.getRol() != null ? usuario.getRol().getNombre() : "SIN_ROL";
        usuario.setRol(nuevoRol);
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Rol del usuario {} cambiado de {} a {}", 
                usuario.getEmail(), rolAnterior, nuevoRol.getNombre());
        
        return mapToUsuarioDetailResponse(usuarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDetailResponse> getUsersByRole(Long roleId) {
        log.debug("Obteniendo usuarios con rol ID: {}", roleId);
        List<Usuario> usuarios = usuarioRepository.findByRolId(roleId);
        return usuarios.stream()
                .map(this::mapToUsuarioDetailResponse)
                .collect(Collectors.toList());
    }

    private UsuarioDetailResponse mapToUsuarioDetailResponse(Usuario usuario) {
        return UsuarioDetailResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? mapRolToResponse(usuario.getRol()) : null)
                .reservasCount(usuario.getReservas() != null ? usuario.getReservas().size() : 0)
                .createdAt(usuario.getCreatedDate())
                .updatedAt(usuario.getModifiedDate())
                .build();
    }

    private edu.usco.campusbookings.application.dto.response.RolResponse mapRolToResponse(Rol rol) {
        return edu.usco.campusbookings.application.dto.response.RolResponse.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.getActivo())
                .usuariosCount(0) // Se puede optimizar si es necesario
                .permissionsCount(rol.getPermissions() != null ? rol.getPermissions().size() : 0)
                .createdAt(rol.getCreatedDate())
                .updatedAt(rol.getModifiedDate())
                .build();
    }
}
