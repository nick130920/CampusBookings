package edu.usco.campusbookings.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.usco.campusbookings.application.dto.request.BuscarEscenariosRequest;
import edu.usco.campusbookings.application.dto.request.EscenarioRequest;
import edu.usco.campusbookings.application.dto.request.FiltrarEscenariosRequest;
import edu.usco.campusbookings.application.dto.response.DetalleEscenarioResponse;
import edu.usco.campusbookings.application.dto.response.EscenarioResponse;
import edu.usco.campusbookings.application.exception.EscenarioNotFoundException;
import edu.usco.campusbookings.application.mapper.EscenarioMapper;
import edu.usco.campusbookings.application.port.input.EscenarioUseCase;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.TipoEscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.UbicacionRepositoryPort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.domain.model.Ubicacion;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing escenarios.
 */
@Service
@RequiredArgsConstructor
public class EscenarioService implements EscenarioUseCase {

    private final EscenarioRepositoryPort escenarioRepositoryPort;
    private final TipoEscenarioRepositoryPort tipoEscenarioRepositoryPort;
    private final UbicacionRepositoryPort ubicacionRepositoryPort;
    private final EscenarioMapper escenarioMapper;

    /**
     * Creates a new escenario.
     *
     * @param escenarioRequest the escenario request input
     * @return the created escenario response
     * @throws IllegalArgumentException if the request is null
     */
    @Override
    @Transactional
    public EscenarioResponse createEscenario(EscenarioRequest escenarioRequest) {
        if (escenarioRequest == null) {
            throw new IllegalArgumentException("El escenario no puede ser nulo");
        }

        // Find or create tipo
        TipoEscenario tipo = tipoEscenarioRepositoryPort.findByNombre(escenarioRequest.getTipo())
                .orElseGet(() -> {
                    TipoEscenario newTipo = new TipoEscenario();
                    newTipo.setNombre(escenarioRequest.getTipo());
                    return tipoEscenarioRepositoryPort.save(newTipo);
                });

        // Find or create ubicacion
        Ubicacion ubicacion = ubicacionRepositoryPort.findByNombre(escenarioRequest.getUbicacion())
                .orElseGet(() -> {
                    Ubicacion newUbicacion = new Ubicacion();
                    newUbicacion.setNombre(escenarioRequest.getUbicacion());
                    return ubicacionRepositoryPort.save(newUbicacion);
                });

        // Map request to entity
        Escenario escenario = escenarioMapper.toEntity(escenarioRequest);
        escenario.setTipo(tipo);
        escenario.setUbicacion(ubicacion);
        
        // Save the escenario
        Escenario savedEscenario = escenarioRepositoryPort.save(escenario);
        
        // Map entity to response
        return escenarioMapper.toDto(savedEscenario);
    }

    /**
     * Creates multiple escenarios.
     *
     * @param escenarioRequests the list of escenario requests
     * @return the list of created escenario responses
     * @throws IllegalArgumentException if the request list is null or empty
     */
    @Override
    @Transactional
    public List<EscenarioResponse> createEscenarios(List<EscenarioRequest> escenarioRequests) {
        if (escenarioRequests == null || escenarioRequests.isEmpty()) {
            throw new IllegalArgumentException("EscenarioRequests cannot be null or empty");
        }

        List<Escenario> escenarios = escenarioRequests.stream()
                .map(escenarioMapper::toEntity)
                .toList();

        escenarios = escenarioRepositoryPort.saveAll(escenarios);

        return escenarioMapper.toDtoList(escenarios);
    }

    /**
     * Retrieves all escenarios.
     *
     * @return the list of escenario responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<EscenarioResponse> findAll() {
        return escenarioRepositoryPort.findAll().stream()
                .map(escenarioMapper::toDto)
                .toList();
    }

    /**
     * Finds a escenario by ID.
     *
     * @param id the escenario ID
     * @return the found escenario response
     * @throws EscenarioNotFoundException if the escenario is not found
     */
    @Override
    @Transactional(readOnly = true)
    public EscenarioResponse findById(Long id) {
        return escenarioRepositoryPort.findById(id)
                .map(escenarioMapper::toDto)
                .orElseThrow(() -> new EscenarioNotFoundException("Escenario not found with ID: " + id));
    }

    /**
     * Updates a escenario.
     *
     * @param id the Address ID
     * @param request the escenario request
     * @return the updated escenario response
     * @throws EscenarioNotFoundException if the escenario is not found
     */
    @Override
    @Transactional
    public EscenarioResponse updateEscenario(Long id, EscenarioRequest request) {
        Escenario escenario = escenarioRepositoryPort.findById(id)
                .orElseThrow(() -> new EscenarioNotFoundException("Escenario no encontrado"));

        // Find or create tipo
        TipoEscenario tipo = tipoEscenarioRepositoryPort.findByNombre(request.getTipo())
                .orElseGet(() -> {
                    TipoEscenario newTipo = new TipoEscenario();
                    newTipo.setNombre(request.getTipo());
                    return tipoEscenarioRepositoryPort.save(newTipo);
                });

        // Find or create ubicacion
        Ubicacion ubicacion = ubicacionRepositoryPort.findByNombre(request.getUbicacion())
                .orElseGet(() -> {
                    Ubicacion newUbicacion = new Ubicacion();
                    newUbicacion.setNombre(request.getUbicacion());
                    return ubicacionRepositoryPort.save(newUbicacion);
                });

        // Actualizar campos del escenario que existen en el modelo
        escenario.setNombre(request.getNombre());
        escenario.setDescripcion(request.getDescripcion());
        escenario.setTipo(tipo);
        escenario.setUbicacion(ubicacion);
        escenario.setCapacidad(request.getCapacidad());
        escenario.setDisponible(request.getDisponible());
        escenario.setRecursos(request.getRecursos());
        escenario.setImagenUrl(request.getImagenUrl());

        Escenario saved = escenarioRepositoryPort.save(escenario);
        return escenarioMapper.toDto(saved);
    }

    /**
     * Deletes a escenario by ID.
     *
     * @param id the escenario ID
     * @throws EscenarioNotFoundException if the escenario is not found
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        escenarioRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenarioResponse> filtrarEscenarios(FiltrarEscenariosRequest request) {
        List<Escenario> allEscenarios = escenarioRepositoryPort.findAll();
        
        return allEscenarios.stream()
            .filter(escenario -> {
                boolean matches = true;
                if (request.getTipo() != null) {
                    matches = escenario.getTipo() != null && 
                            escenario.getTipo().getNombre().equalsIgnoreCase(request.getTipo());
                }
                if (request.getNombre() != null && matches) {
                    matches = escenario.getNombre() != null && 
                            escenario.getNombre().equalsIgnoreCase(request.getNombre());
                }
                if (request.getUbicacion() != null && matches) {
                    matches = escenario.getUbicacion() != null && 
                            escenario.getUbicacion().getNombre() != null &&
                            escenario.getUbicacion().getNombre().equalsIgnoreCase(request.getUbicacion());
                }
                return matches;
            })
            .map(escenarioMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenarioResponse> buscarEscenarios(BuscarEscenariosRequest request) {
        List<Escenario> allEscenarios = escenarioRepositoryPort.findAll();
        
        return allEscenarios.stream()
            .filter(escenario -> {
                boolean matches = false;
                if (request.getNombre() != null) {
                    matches = escenario.getNombre() != null && 
                            escenario.getNombre().toLowerCase().contains(request.getNombre().toLowerCase());
                }
                if (!matches && request.getUbicacion() != null) {
                    matches = escenario.getUbicacion() != null && 
                            escenario.getUbicacion().getNombre() != null &&
                            escenario.getUbicacion().getNombre().toLowerCase().contains(request.getUbicacion().toLowerCase());
                }
                if (!matches && request.getTipo() != null) {
                    matches = escenario.getTipo() != null && 
                            escenario.getTipo().getNombre() != null &&
                            escenario.getTipo().getNombre().toLowerCase().contains(request.getTipo().toLowerCase());
                }
                return matches;
            })
            .map(escenarioMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleEscenarioResponse obtenerDetalles(Long id) {
        Escenario escenario = escenarioRepositoryPort.findById(id)
                .orElseThrow(() -> new EscenarioNotFoundException("Escenario not found with ID: " + id));
        return escenarioMapper.toDetalleResponse(escenario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getTiposEscenario() {
        return tipoEscenarioRepositoryPort.findAll().stream()
                .map(TipoEscenario::getNombre)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getUbicaciones() {
        return ubicacionRepositoryPort.findAll().stream()
                .map(Ubicacion::getNombre)
                .distinct()
                .collect(Collectors.toList());
    }
}