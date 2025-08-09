package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.port.input.ScenarioTypePermissionUseCase;
import edu.usco.campusbookings.application.port.output.ScenarioTypePermissionRepositoryPort;
import edu.usco.campusbookings.application.port.output.TipoEscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.ScenarioTypePermission;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScenarioTypePermissionService implements ScenarioTypePermissionUseCase {

    private final ScenarioTypePermissionRepositoryPort repository;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final TipoEscenarioRepositoryPort tipoEscenarioRepositoryPort;

    @Override
    @Transactional
    public ScenarioTypePermission assignPermissionToUser(String userEmail, String tipoNombre, String action) {
        Usuario usuario = usuarioRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        TipoEscenario tipo = tipoEscenarioRepositoryPort.findByNombre(tipoNombre)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de escenario no encontrado"));

        ScenarioTypePermission perm = new ScenarioTypePermission();
        perm.setUsuario(usuario);
        perm.setTipoEscenario(tipo);
        perm.setAction(action);
        return repository.save(perm);
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
    public List<ScenarioTypePermission> getPermissionsForUser(String userEmail) {
        return repository.findByUsuarioEmail(userEmail);
    }
}


