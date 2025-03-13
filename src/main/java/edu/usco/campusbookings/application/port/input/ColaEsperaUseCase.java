package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ColaEsperaRequest;
import edu.usco.campusbookings.application.dto.response.ColaEsperaResponse;

import java.util.List;

public interface ColaEsperaUseCase {

    /**
        * Find a ColaEspera by id.
        *
        * @param id a id
        * @return a ColaEsperaResponse
    */
	ColaEsperaResponse findById(Long id);

	/**
    	 * Get a list of all colaesperas.
    	 *
    	 * @return a list of ColaEsperaResponses
    */
    List<ColaEsperaResponse> findAll();

	/**
	 * Create a list of colaesperas.
	 *
	 * @param requests a list of ColaEsperaRequests
	 * @return a list of ColaEsperaResponses
	 */
	List<ColaEsperaResponse> createColaEsperas(List<ColaEsperaRequest> requests);

	/**
	 * Create a ColaEspera.
	 *
	 * @param colaesperaRequest a ColaEsperaRequest
	 * @return a ColaEsperaResponse
	 */
	ColaEsperaResponse createColaEspera(ColaEsperaRequest colaesperaRequest);



	/**
	 * Update a ColaEspera.
	 *
	 * @param id the Address ID
	 * @param request an ColaEsperaRequest
	 * @return a ColaEsperaResponse
	 */
	ColaEsperaResponse updateColaEspera(Long id, ColaEsperaRequest request);

    /**
         * Delete a ColaEspera.
         *
         * @param id an id
     */
	void deleteById(Long id);





}
