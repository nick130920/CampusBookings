package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.ScenarioTypePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataScenarioTypePermissionRepository extends JpaRepository<ScenarioTypePermission, Long> {

    @Query("select p from ScenarioTypePermission p where p.usuario.email = :email")
    List<ScenarioTypePermission> findByUsuarioEmail(@Param("email") String email);

    @Query("select count(p) > 0 from ScenarioTypePermission p where p.usuario.email = :email and lower(p.tipoEscenario.nombre) = lower(:tipoNombre) and p.action = :action")
    boolean existsByUsuarioEmailAndTipoNombreAndAction(@Param("email") String email,
                                                       @Param("tipoNombre") String tipoNombre,
                                                       @Param("action") String action);
    
    @Query("select p from ScenarioTypePermission p where p.usuario.email = :email and lower(p.tipoEscenario.nombre) = lower(:tipoNombre) and p.action = :action")
    Optional<ScenarioTypePermission> findByUsuarioEmailAndTipoNombreAndAction(@Param("email") String email,
                                                                              @Param("tipoNombre") String tipoNombre,
                                                                              @Param("action") String action);
    
    @Modifying
    @Query("delete from ScenarioTypePermission p where p.usuario.email = :email and lower(p.tipoEscenario.nombre) = lower(:tipoNombre) and p.action = :action")
    void deleteByUsuarioEmailAndTipoNombreAndAction(@Param("email") String email,
                                                    @Param("tipoNombre") String tipoNombre,
                                                    @Param("action") String action);
}


