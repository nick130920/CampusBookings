package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.ConfiguracionSistemaRepositoryPort;
import edu.usco.campusbookings.domain.model.ConfiguracionSistema;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.repository.ConfiguracionSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConfiguracionSistemaRepositoryAdapter implements ConfiguracionSistemaRepositoryPort {

    private final ConfiguracionSistemaRepository repository;

    @Autowired
    public ConfiguracionSistemaRepositoryAdapter(ConfiguracionSistemaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ConfiguracionSistema> obtenerConfiguracionReservas() {
        return repository.findByTipoConfiguracion(ConfiguracionSistema.TipoConfiguracion.RESERVAS);
    }

    @Override
    public ConfiguracionSistema guardarConfiguracion(ConfiguracionSistema configuracion) {
        return repository.save(configuracion);
    }

    @Override
    public Optional<ConfiguracionSistema> obtenerPorTipo(ConfiguracionSistema.TipoConfiguracion tipo) {
        return repository.findByTipoConfiguracion(tipo);
    }

    @Override
    public boolean existeConfiguracionReservas() {
        return repository.existsByTipoConfiguracion(ConfiguracionSistema.TipoConfiguracion.RESERVAS);
    }
}