package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.UsuarioRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioResponse;
import edu.usco.campusbookings.application.mapper.UsuarioMapper;
import edu.usco.campusbookings.application.port.input.UsuarioUseCase;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.application.exception.UsuarioNotFoundException;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing usuarios.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioMapper usuarioMapper;

    /**
     * Creates a new usuario.
     *
     * @param usuarioRequest the usuario request input
     * @return the created usuario response
     * @throws IllegalArgumentException if the request is null
     */
    @Override
    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest usuarioRequest) {
        if (usuarioRequest == null) {
            throw new IllegalArgumentException("UsuarioRequest cannot be null");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioRequest);
        usuario = usuarioRepositoryPort.save(usuario);

        return usuarioMapper.toDto(usuario);
    }

    /**
     * Creates multiple usuarios.
     *
     * @param usuarioRequests the list of usuario requests
     * @return the list of created usuario responses
     * @throws IllegalArgumentException if the request list is null or empty
     */
    @Override
    @Transactional
    public List<UsuarioResponse> createUsuarios(List<UsuarioRequest> usuarioRequests) {
        if (usuarioRequests == null || usuarioRequests.isEmpty()) {
            throw new IllegalArgumentException("UsuarioRequests cannot be null or empty");
        }

        List<Usuario> usuarios = usuarioRequests.stream()
                .map(usuarioMapper::toEntity)
                .toList();

        usuarios = usuarioRepositoryPort.saveAll(usuarios);

        return usuarioMapper.toDtoList(usuarios);
    }

    /**
     * Retrieves all usuarios.
     *
     * @return the list of usuario responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepositoryPort.findAll().stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    /**
     * Finds a usuario by ID.
     *
     * @param id the usuario ID
     * @return the found usuario response
     * @throws UsuarioNotFoundException if the usuario is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        return usuarioRepositoryPort.findById(id)
                .map(usuarioMapper::toDto)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario not found with ID: " + id));
    }

    /**
     * Updates a usuario.
     *
     * @param id the Address ID
     * @param request the usuario request
     * @return the updated usuario response
     * @throws UsuarioNotFoundException if the usuario is not found
     */
    @Override
    @Transactional
    public UsuarioResponse updateUsuario(Long id, UsuarioRequest request) {
        usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario not found with ID: " + id));


        Usuario saved = usuarioRepositoryPort.save(usuarioMapper.toEntity(request));

        return usuarioMapper.toDto(saved);
    }

    /**
     * Deletes a usuario by ID.
     *
     * @param id the usuario ID
     * @throws UsuarioNotFoundException if the usuario is not found
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        usuarioRepositoryPort.deleteById(id);
    }
}