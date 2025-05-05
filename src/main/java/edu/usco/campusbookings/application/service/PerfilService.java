package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ActualizarPerfilRequest;
import edu.usco.campusbookings.application.dto.response.PerfilResponse;
import edu.usco.campusbookings.application.mapper.PerfilMapper;
import edu.usco.campusbookings.application.port.input.PerfilUseCase;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerfilService implements PerfilUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final PerfilMapper perfilMapper;

    @Override
    @Transactional(readOnly = true)
    public PerfilResponse obtenerPerfil() {
        // Obtener el usuario actual
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepositoryPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return perfilMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public PerfilResponse actualizarPerfil(ActualizarPerfilRequest request) {
        // Obtener el usuario actual
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioRepositoryPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el correo electrónico ya está en uso por otro usuario
        if (!usuario.getEmail().equals(request.getEmail()) && 
            usuarioRepositoryPort.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }

        // Actualizar los campos del usuario
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // Guardar los cambios
        Usuario usuarioActualizado = usuarioRepositoryPort.save(usuario);
        return perfilMapper.toResponse(usuarioActualizado);
    }
}
