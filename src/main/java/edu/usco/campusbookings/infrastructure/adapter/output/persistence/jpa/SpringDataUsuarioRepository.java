package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por email cargando eagerly el rol y sus permisos
     * Evita el problema de LazyInitializationException usando EntityGraph
     */
    @EntityGraph(attributePaths = {"rol", "rol.permissions"})
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> findByEmailWithPermissions(@Param("email") String email);

    List<Usuario> getUsuariosByRol_Nombre(String rol);

    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Usuario> searchUsuarios(@Param("searchTerm") String searchTerm);

    List<Usuario> findByRolId(Long rolId);
}
