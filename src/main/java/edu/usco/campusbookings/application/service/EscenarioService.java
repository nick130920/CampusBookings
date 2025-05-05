package edu.usco.campusbookings.application.service;

import java.util.List;

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
import edu.usco.campusbookings.domain.model.Escenario;
import lombok.RequiredArgsConstructor;

/**
 * Service implementation for managing escenarios.
 */
@Service
@RequiredArgsConstructor
public class EscenarioService implements EscenarioUseCase {

    private final EscenarioRepositoryPort escenarioRepositoryPort;
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
            throw new IllegalArgumentException("EscenarioRequest cannot be null");
        }

        Escenario escenario = escenarioMapper.toEntity(escenarioRequest);
        escenario = escenarioRepositoryPort.save(escenario);

        return escenarioMapper.toDto(escenario);
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

        escenario.setNombre(request.getNombre());
        escenario.setTipo(request.getTipo());
        escenario.setUbicacion(request.getUbicacion());
        escenario.setHorariosDisponibles(request.getHorariosDisponibles());
        escenario.setReservas(request.getReservas());

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
        List<Escenario> escenarios = escenarioRepositoryPort.findByTipoOrNombreOrUbicacion(
            request.getTipo(),
            request.getNombre(),
            request.getUbicacion()
        );
        return escenarioMapper.toDtoList(escenarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenarioResponse> buscarEscenarios(BuscarEscenariosRequest request) {
        List<Escenario> escenarios = escenarioRepositoryPort.findByNombreContainingOrUbicacionContainingOrTipoContaining(
            request.getNombre(),
            request.getUbicacion(),
            request.getTipo()
        );
        return escenarioMapper.toDtoList(escenarios);
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleEscenarioResponse obtenerDetalles(Long id) {
        Escenario escenario = escenarioRepositoryPort.findById(id)
                .orElseThrow(() -> new EscenarioNotFoundException("Escenario not found with ID: " + id));
        return escenarioMapper.toDetalleResponse(escenario);
    }
}