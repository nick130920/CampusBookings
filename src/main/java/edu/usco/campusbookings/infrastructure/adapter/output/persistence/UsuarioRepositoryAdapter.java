package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> saveAll(List<Usuario> usuarios) {
        return usuarioRepository.saveAll(usuarios);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> getUsuariosByRole(String role) {
        return usuarioRepository.getUsuariosByRol_Nombre(role);
    }

    @Override
    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public List<Usuario> searchUsuarios(String searchTerm) {
        return usuarioRepository.searchUsuarios(searchTerm);
    }

    @Override
    public List<Usuario> findByRolId(Long roleId) {
        return usuarioRepository.findByRolId(roleId);
    }
}
