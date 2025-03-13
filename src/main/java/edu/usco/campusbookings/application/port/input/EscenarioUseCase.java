package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.EscenarioRequest;
import edu.usco.campusbookings.application.dto.response.EscenarioResponse;

import java.util.List;

public interface EscenarioUseCase {

    /**
        * Find a Escenario by id.
        *
        * @param id a id
        * @return a EscenarioResponse
    */
	EscenarioResponse findById(Long id);

	/**
    	 * Get a list of all escenarios.
    	 *
    	 * @return a list of EscenarioResponses
    */
    List<EscenarioResponse> findAll();

	/**
	 * Create a list of escenarios.
	 *
	 * @param requests a list of EscenarioRequests
	 * @return a list of EscenarioResponses
	 */
	List<EscenarioResponse> createEscenarios(List<EscenarioRequest> requests);

	/**
	 * Create a Escenario.
	 *
	 * @param escenarioRequest a EscenarioRequest
	 * @return a EscenarioResponse
	 */
	EscenarioResponse createEscenario(EscenarioRequest escenarioRequest);



	/**
	 * Update a Escenario.
	 *
	 * @param id the Address ID
	 * @param request an EscenarioRequest
	 * @return a EscenarioResponse
	 */
	EscenarioResponse updateEscenario(Long id, EscenarioRequest request);

    /**
         * Delete a Escenario.
         *
         * @param id an id
     */
	void deleteById(Long id);





}
