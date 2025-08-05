package edu.usco.campusbookings.infrastructure.adapter.output.persistence.repository;

import edu.usco.campusbookings.domain.model.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Long> {

    /**
     * Buscar configuración por tipo
     */
    Optional<ConfiguracionSistema> findByTipoConfiguracion(ConfiguracionSistema.TipoConfiguracion tipoConfiguracion);

    /**
     * Verificar si existe configuración por tipo
     */
    boolean existsByTipoConfiguracion(ConfiguracionSistema.TipoConfiguracion tipoConfiguracion);

    /**
     * Obtener la configuración de reservas más reciente
     */
    @Query("SELECT c FROM ConfiguracionSistema c WHERE c.tipoConfiguracion = :tipo ORDER BY c.modifiedDate DESC")
    Optional<ConfiguracionSistema> findLatestByTipo(@Param("tipo") ConfiguracionSistema.TipoConfiguracion tipo);

    /**
     * Eliminar configuración por tipo
     */
    void deleteByTipoConfiguracion(ConfiguracionSistema.TipoConfiguracion tipoConfiguracion);
}