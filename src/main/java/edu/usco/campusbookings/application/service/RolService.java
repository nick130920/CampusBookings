package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.RolRequest;
import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.application.mapper.RolMapper;
import edu.usco.campusbookings.application.port.input.RolUseCase;
import edu.usco.campusbookings.application.port.output.RolRepositoryPort;
import edu.usco.campusbookings.application.exception.RolNotFoundException;
import edu.usco.campusbookings.domain.model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing rols.
 */
@Service
@RequiredArgsConstructor
public class RolService implements RolUseCase {

    private final RolRepositoryPort rolRepositoryPort;
    private final RolMapper rolMapper;

    /**
     * Creates a new rol.
     *
     * @param rolRequest the rol request input
     * @return the created rol response
     * @throws IllegalArgumentException if the request is null
     */
    @Override
    @Transactional
    public RolResponse createRol(RolRequest rolRequest) {
        if (rolRequest == null) {
            throw new IllegalArgumentException("RolRequest cannot be null");
        }

        Rol rol = rolMapper.toEntity(rolRequest);
        rol = rolRepositoryPort.save(rol);

        return rolMapper.toDto(rol);
    }

    /**
     * Creates multiple rols.
     *
     * @param rolRequests the list of rol requests
     * @return the list of created rol responses
     * @throws IllegalArgumentException if the request list is null or empty
     */
    @Override
    @Transactional
    public List<RolResponse> createRols(List<RolRequest> rolRequests) {
        if (rolRequests == null || rolRequests.isEmpty()) {
            throw new IllegalArgumentException("RolRequests cannot be null or empty");
        }

        List<Rol> rols = rolRequests.stream()
                .map(rolMapper::toEntity)
                .toList();

        rols = rolRepositoryPort.saveAll(rols);

        return rolMapper.toDtoList(rols);
    }

    /**
     * Retrieves all rols.
     *
     * @return the list of rol responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<RolResponse> findAll() {
        return rolRepositoryPort.findAll().stream()
                .map(rolMapper::toDto)
                .toList();
    }

    /**
     * Finds a rol by ID.
     *
     * @param id the rol ID
     * @return the found rol response
     * @throws RolNotFoundException if the rol is not found
     */
    @Override
    @Transactional(readOnly = true)
    public RolResponse findById(Long id) {
        return rolRepositoryPort.findById(id)
                .map(rolMapper::toDto)
                .orElseThrow(() -> new RolNotFoundException("Rol not found with ID: " + id));
    }

    /**
     * Updates a rol.
     *
     * @param id the Address ID
     * @param request the rol request
     * @return the updated rol response
     * @throws RolNotFoundException if the rol is not found
     */
    @Override
    @Transactional
    public RolResponse updateRol(Long id, RolRequest request) {
        rolRepositoryPort.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found with ID: " + id));


        Rol saved = rolRepositoryPort.save(rolMapper.toEntity(request));

        return rolMapper.toDto(saved);
    }

    /**
     * Deletes a rol by ID.
     *
     * @param id the rol ID
     * @throws RolNotFoundException if the rol is not found
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        rolRepositoryPort.deleteById(id);
    }
}