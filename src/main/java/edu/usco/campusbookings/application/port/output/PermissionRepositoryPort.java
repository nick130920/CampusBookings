package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepositoryPort {
    
    Permission save(Permission permission);
    
    Optional<Permission> findById(Long id);
    
    Optional<Permission> findByName(String name);
    
    List<Permission> findAll();
    
    List<Permission> findByResource(String resource);
    
    List<Permission> findByAction(String action);
    
    Optional<Permission> findByResourceAndAction(String resource, String action);
    
    List<Permission> searchPermissions(String searchTerm);
    
    void delete(Permission permission);
    
    void deleteById(Long id);
    
    boolean existsByName(String name);
}
