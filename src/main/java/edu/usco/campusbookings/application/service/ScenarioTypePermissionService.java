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

    private final ScenarioTypePermissionRepositoryPort scenarioTypePermissionRepositoryPort;
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
        
        ScenarioTypePermission saved = scenarioTypePermissionRepositoryPort.save(perm);
        log.info("Permiso asignado exitosamente con ID: {}", saved.getId());
        
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void revokePermissionFromUser(String userEmail, String tipoNombre, String action) {
        log.info("Revocando permiso {} para tipo {} del usuario {}", action, tipoNombre, userEmail);
        
        // Verificar que el usuario existe
        usuarioRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userEmail));

        // Verificar que el tipo de escenario existe
        tipoEscenarioRepositoryPort.findByNombre(tipoNombre)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de escenario no encontrado: " + tipoNombre));

        // Buscar el permiso específico
        ScenarioTypePermission permiso = scenarioTypePermissionRepositoryPort
                .findByUsuarioEmailAndTipoNombreAndAction(userEmail, tipoNombre, action)
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("No se encontró el permiso %s para el tipo %s del usuario %s", 
                                  action, tipoNombre, userEmail)));

        // Eliminar el permiso
        scenarioTypePermissionRepositoryPort.delete(permiso);
        
        log.info("Permiso {} para tipo {} revocado exitosamente del usuario {}", action, tipoNombre, userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioTypePermissionResponse> getPermissionsForUser(String userEmail) {
        log.info("Obteniendo permisos por tipo para usuario: {}", userEmail);
        List<ScenarioTypePermission> permissions = scenarioTypePermissionRepositoryPort.findByUsuarioEmail(userEmail);
        log.info("Permisos obtenidos para usuario: {} - {}", userEmail, permissions.toString());
        return mapper.toResponseList(permissions);
    }
}


