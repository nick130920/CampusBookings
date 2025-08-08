package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataRolRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByNombre(String nombre);
    
    List<Rol> findByActivoTrue();
    
    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<Rol> findByIdWithPermissions(@Param("id") Long id);
    
    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.permissions WHERE r.nombre = :nombre")
    Optional<Rol> findByNombreWithPermissions(@Param("nombre") String nombre);
    
    @Query("SELECT r FROM Rol r WHERE r.nombre LIKE %:searchTerm% OR r.descripcion LIKE %:searchTerm%")
    List<Rol> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.permissions")
    List<Rol> findAllWithPermissions();
}
