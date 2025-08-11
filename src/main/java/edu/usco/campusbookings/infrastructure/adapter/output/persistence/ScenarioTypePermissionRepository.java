package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.ScenarioTypePermissionRepositoryPort;
import edu.usco.campusbookings.domain.model.ScenarioTypePermission;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataScenarioTypePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScenarioTypePermissionRepository implements ScenarioTypePermissionRepositoryPort {

    private final SpringDataScenarioTypePermissionRepository springRepo;

    @Override
    public ScenarioTypePermission save(ScenarioTypePermission permission) {
        return springRepo.save(permission);
    }

    @Override
    public List<ScenarioTypePermission> findByUsuarioEmail(String email) {
        return springRepo.findByUsuarioEmail(email);
    }

    @Override
    public boolean existsByUsuarioEmailAndTipoNombreAndAction(String email, String tipoNombre, String action) {
        return springRepo.existsByUsuarioEmailAndTipoNombreAndAction(email, tipoNombre, action);
    }
    
    @Override
    public Optional<ScenarioTypePermission> findByUsuarioEmailAndTipoNombreAndAction(String email, String tipoNombre, String action) {
        return springRepo.findByUsuarioEmailAndTipoNombreAndAction(email, tipoNombre, action);
    }
    
    @Override
    public void delete(ScenarioTypePermission permission) {
        springRepo.delete(permission);
    }
    
    @Override
    public void deleteByUsuarioEmailAndTipoNombreAndAction(String email, String tipoNombre, String action) {
        springRepo.deleteByUsuarioEmailAndTipoNombreAndAction(email, tipoNombre, action);
    }
}


