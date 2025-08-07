package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.CreateRolRequest;
import edu.usco.campusbookings.application.dto.request.UpdateRolRequest;
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
import java.util.Optional;

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
    public RolResponse createRol(CreateRolRequest rolRequest) {
        if (rolRequest == null) {
            throw new IllegalArgumentException("CreateRolRequest cannot be null");
        }

        Rol rol = rolMapper.toEntity(rolRequest);
        rol = rolRepositoryPort.save(rol);

        return rolMapper.toResponse(rol);
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
    public List<RolResponse> createRols(List<CreateRolRequest> rolRequests) {
        if (rolRequests == null || rolRequests.isEmpty()) {
            throw new IllegalArgumentException("CreateRolRequests cannot be null or empty");
        }

        List<Rol> rols = rolRequests.stream()
                .map(rolMapper::toEntity)
                .toList();

        rols = rolRepositoryPort.saveAll(rols);

        return rolMapper.toResponseList(rols);
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
                .map(rolMapper::toResponse)
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
                .map(rolMapper::toResponse)
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
    public RolResponse updateRol(Long id, UpdateRolRequest request) {
        Rol existingRol = rolRepositoryPort.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol not found with ID: " + id));

        // Update fields
        existingRol.setNombre(request.getNombre());
        existingRol.setDescripcion(request.getDescripcion());
        if (request.getActivo() != null) {
            existingRol.setActivo(request.getActivo());
        }

        Rol saved = rolRepositoryPort.save(existingRol);
        return rolMapper.toResponse(saved);
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

    /**
     * Finds a rol by nombre.
     *
     * @param nombre the rol nombre
     * @return the found rol if exists, empty optional otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> findByNombre(String nombre) {
        return rolRepositoryPort.findByNombre(nombre);
    }
}