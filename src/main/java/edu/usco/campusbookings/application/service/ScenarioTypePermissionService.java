package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.response.ScenarioTypePermissionResponse;
import edu.usco.campusbookings.application.mapper.ScenarioTypePermissionMapper;
import edu.usco.campusbookings.application.port.input.ScenarioTypePermissionUseCase;
import edu.usco.campusbookings.application.port.output.ScenarioTypePermissionRepositoryPort;
import edu.usco.campusbookings.application.port.output.TipoEscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.ScenarioTypePermission;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioTypePermissionService implements ScenarioTypePermissionUseCase {

    private final ScenarioTypePermissionRepositoryPort repository;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TipoEscenarioRepositoryPort tipoEscenarioRepositoryPort;
    private final ScenarioTypePermissionMapper mapper;

    @Override
    @Transactional
    public ScenarioTypePermissionResponse assignPermissionToUser(String userEmail, String tipoNombre, String action) {
        log.info("Asignando permiso {} para tipo {} al usuario {}", action, tipoNombre, userEmail);
        
        Usuario usuario = usuarioRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userEmail));

        TipoEscenario tipo = tipoEscenarioRepositoryPort.findByNombre(tipoNombre)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de escenario no encontrado: " + tipoNombre));

        ScenarioTypePermission perm = new ScenarioTypePermission();
        perm.setUsuario(usuario);
        perm.setTipoEscenario(tipo);
        perm.setAction(action);
        perm.setActive(true);
        
        ScenarioTypePermission saved = repository.save(perm);
        log.info("Permiso asignado exitosamente con ID: {}", saved.getId());
        
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void revokePermissionFromUser(String userEmail, String tipoNombre, String action) {
        // Simplificación: en una implementación completa, se buscaría y eliminaría.
        // Aquí, podría añadirse un método delete en el repositorio si se requiere.
        throw new UnsupportedOperationException("Revocar permisos aún no implementado");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioTypePermissionResponse> getPermissionsForUser(String userEmail) {
        log.info("Obteniendo permisos por tipo para usuario: {}", userEmail);
        List<ScenarioTypePermission> permissions = repository.findByUsuarioEmail(userEmail);
        return mapper.toResponseList(permissions);
    }
}


