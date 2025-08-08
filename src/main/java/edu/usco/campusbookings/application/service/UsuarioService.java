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

    private final UsuarioRepositoryPort usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    /**
     * Creates a new usuario.
     *
     * @param request the usuario request input
     * @return the created usuario response
     * @throws IllegalArgumentException if the request is null
     */
    @Override
    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UsuarioRequest cannot be null");
        }

        Usuario usuario = usuarioMapper.toDomain(request);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
    }

    /**
     * Creates multiple usuarios.
     *
     * @param requests the list of usuario requests
     * @return the list of created usuario responses
     * @throws IllegalArgumentException if the request list is null or empty
     */
    @Override
    @Transactional
    public List<UsuarioResponse> createUsuarios(List<UsuarioRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("UsuarioRequests cannot be null or empty");
        }

        List<Usuario> usuarios = requests.stream()
                .map(usuarioMapper::toDomain)
                .toList();

        usuarios = usuarioRepository.saveAll(usuarios);
        return usuarios.stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves all usuarios.
     *
     * @return the list of usuario responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
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
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> UsuarioNotFoundException.withId(id));
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
        if (!usuarioRepository.existsById(id)) {
            throw UsuarioNotFoundException.withId(id);
        }

        Usuario usuario = usuarioMapper.toDomain(request);
        usuario.setId(id);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
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
        if (!usuarioRepository.existsById(id)) {
            throw UsuarioNotFoundException.withId(id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> UsuarioNotFoundException.withEmail(email));
    }

    @Override
    @Transactional
    public Usuario update(Long id, Usuario usuario) {
        if (!usuarioRepository.existsById(id)) {
            throw UsuarioNotFoundException.withId(id);
        }
        usuario.setId(id);
        return usuarioRepository.save(usuario);
    }
}