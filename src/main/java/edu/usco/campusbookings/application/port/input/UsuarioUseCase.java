package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.UsuarioRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioResponse;
import edu.usco.campusbookings.domain.model.Usuario;

import java.util.List;

public interface UsuarioUseCase {
    UsuarioResponse createUsuario(UsuarioRequest request);
    List<UsuarioResponse> createUsuarios(List<UsuarioRequest> requests);
    UsuarioResponse findById(Long id);
    List<UsuarioResponse> findAll();
    UsuarioResponse updateUsuario(Long id, UsuarioRequest request);
    void deleteById(Long id);
    Usuario save(Usuario usuario);
    Usuario findByEmail(String email);
    Usuario update(Long id, Usuario usuario);
}
