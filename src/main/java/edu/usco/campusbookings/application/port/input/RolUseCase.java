package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.RolRequest;
import edu.usco.campusbookings.application.dto.response.RolResponse;

import java.util.List;

public interface RolUseCase {

    /**
        * Find a Rol by id.
        *
        * @param id a id
        * @return a RolResponse
    */
	RolResponse findById(Long id);

	/**
    	 * Get a list of all rols.
    	 *
    	 * @return a list of RolResponses
    */
    List<RolResponse> findAll();

	/**
	 * Create a list of rols.
	 *
	 * @param requests a list of RolRequests
	 * @return a list of RolResponses
	 */
	List<RolResponse> createRols(List<RolRequest> requests);

	/**
	 * Create a Rol.
	 *
	 * @param rolRequest a RolRequest
	 * @return a RolResponse
	 */
	RolResponse createRol(RolRequest rolRequest);



	/**
	 * Update a Rol.
	 *
	 * @param id the Address ID
	 * @param request an RolRequest
	 * @return a RolResponse
	 */
	RolResponse updateRol(Long id, RolRequest request);

    /**
         * Delete a Rol.
         *
         * @param id an id
     */
	void deleteById(Long id);





}
