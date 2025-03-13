package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ColaEsperaRequest;
import edu.usco.campusbookings.application.dto.response.ColaEsperaResponse;
import edu.usco.campusbookings.application.mapper.ColaEsperaMapper;
import edu.usco.campusbookings.application.port.input.ColaEsperaUseCase;
import edu.usco.campusbookings.application.port.output.ColaEsperaRepositoryPort;
import edu.usco.campusbookings.application.exception.ColaEsperaNotFoundException;
import edu.usco.campusbookings.domain.model.ColaEspera;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing colaesperas.
 */
@Service
@RequiredArgsConstructor
public class ColaEsperaService implements ColaEsperaUseCase {

    private final ColaEsperaRepositoryPort colaesperaRepositoryPort;
    private final ColaEsperaMapper colaesperaMapper;

    /**
     * Creates a new colaespera.
     *
     * @param colaesperaRequest the colaespera request input
     * @return the created colaespera response
     * @throws IllegalArgumentException if the request is null
     */
    @Override
    @Transactional
    public ColaEsperaResponse createColaEspera(ColaEsperaRequest colaesperaRequest) {
        if (colaesperaRequest == null) {
            throw new IllegalArgumentException("ColaEsperaRequest cannot be null");
        }

        ColaEspera colaespera = colaesperaMapper.toEntity(colaesperaRequest);
        colaespera = colaesperaRepositoryPort.save(colaespera);

        return colaesperaMapper.toDto(colaespera);
    }

    /**
     * Creates multiple colaesperas.
     *
     * @param colaesperaRequests the list of colaespera requests
     * @return the list of created colaespera responses
     * @throws IllegalArgumentException if the request list is null or empty
     */
    @Override
    @Transactional
    public List<ColaEsperaResponse> createColaEsperas(List<ColaEsperaRequest> colaesperaRequests) {
        if (colaesperaRequests == null || colaesperaRequests.isEmpty()) {
            throw new IllegalArgumentException("ColaEsperaRequests cannot be null or empty");
        }

        List<ColaEspera> colaesperas = colaesperaRequests.stream()
                .map(colaesperaMapper::toEntity)
                .toList();

        colaesperas = colaesperaRepositoryPort.saveAll(colaesperas);

        return colaesperaMapper.toDtoList(colaesperas);
    }

    /**
     * Retrieves all colaesperas.
     *
     * @return the list of colaespera responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<ColaEsperaResponse> findAll() {
        return colaesperaRepositoryPort.findAll().stream()
                .map(colaesperaMapper::toDto)
                .toList();
    }

    /**
     * Finds a colaespera by ID.
     *
     * @param id the colaespera ID
     * @return the found colaespera response
     * @throws ColaEsperaNotFoundException if the colaespera is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ColaEsperaResponse findById(Long id) {
        return colaesperaRepositoryPort.findById(id)
                .map(colaesperaMapper::toDto)
                .orElseThrow(() -> new ColaEsperaNotFoundException("ColaEspera not found with ID: " + id));
    }

    /**
     * Updates a colaespera.
     *
     * @param id the Address ID
     * @param request the colaespera request
     * @return the updated colaespera response
     * @throws ColaEsperaNotFoundException if the colaespera is not found
     */
    @Override
    @Transactional
    public ColaEsperaResponse updateColaEspera(Long id, ColaEsperaRequest request) {
        colaesperaRepositoryPort.findById(id)
                .orElseThrow(() -> new ColaEsperaNotFoundException("ColaEspera not found with ID: " + id));


        ColaEspera saved = colaesperaRepositoryPort.save(colaesperaMapper.toEntity(request));

        return colaesperaMapper.toDto(saved);
    }

    /**
     * Deletes a colaespera by ID.
     *
     * @param id the colaespera ID
     * @throws ColaEsperaNotFoundException if the colaespera is not found
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        colaesperaRepositoryPort.deleteById(id);
    }
}