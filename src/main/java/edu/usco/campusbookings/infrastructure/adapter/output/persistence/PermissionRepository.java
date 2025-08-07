package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.PermissionRepositoryPort;
import edu.usco.campusbookings.domain.model.Permission;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PermissionRepository implements PermissionRepositoryPort {

    private final SpringDataPermissionRepository springDataPermissionRepository;

    @Override
    public Permission save(Permission permission) {
        return springDataPermissionRepository.save(permission);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return springDataPermissionRepository.findById(id);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return springDataPermissionRepository.findByName(name);
    }

    @Override
    public List<Permission> findAll() {
        return springDataPermissionRepository.findAll();
    }

    @Override
    public List<Permission> findByResource(String resource) {
        return springDataPermissionRepository.findByResource(resource);
    }

    @Override
    public List<Permission> findByAction(String action) {
        return springDataPermissionRepository.findByAction(action);
    }

    @Override
    public Optional<Permission> findByResourceAndAction(String resource, String action) {
        return springDataPermissionRepository.findByResourceAndAction(resource, action);
    }

    @Override
    public List<Permission> searchPermissions(String searchTerm) {
        return springDataPermissionRepository.findBySearchTerm(searchTerm);
    }

    @Override
    public void delete(Permission permission) {
        springDataPermissionRepository.delete(permission);
    }

    @Override
    public void deleteById(Long id) {
        springDataPermissionRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return springDataPermissionRepository.findByName(name).isPresent();
    }
}
