package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.UsuarioRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioUseCase {

    /**
        * Find a Usuario by id.
        *
        * @param id a id
        * @return a UsuarioResponse
    */
	UsuarioResponse findById(Long id);

	/**
    	 * Get a list of all usuarios.
    	 *
    	 * @return a list of UsuarioResponses
    */
    List<UsuarioResponse> findAll();

	/**
	 * Create a list of usuarios.
	 *
	 * @param requests a list of UsuarioRequests
	 * @return a list of UsuarioResponses
	 */
	List<UsuarioResponse> createUsuarios(List<UsuarioRequest> requests);

	/**
	 * Create a Usuario.
	 *
	 * @param usuarioRequest a UsuarioRequest
	 * @return a UsuarioResponse
	 */
	UsuarioResponse createUsuario(UsuarioRequest usuarioRequest);



	/**
	 * Update a Usuario.
	 *
	 * @param id the Address ID
	 * @param request an UsuarioRequest
	 * @return a UsuarioResponse
	 */
	UsuarioResponse updateUsuario(Long id, UsuarioRequest request);

    /**
         * Delete a Usuario.
         *
         * @param id an id
     */
	void deleteById(Long id);





}
