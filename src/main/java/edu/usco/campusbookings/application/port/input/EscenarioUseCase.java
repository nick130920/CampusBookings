package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.BuscarEscenariosRequest;
import edu.usco.campusbookings.application.dto.request.EscenarioRequest;
import edu.usco.campusbookings.application.dto.request.FiltrarEscenariosRequest;
import edu.usco.campusbookings.application.dto.response.DetalleEscenarioResponse;
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
     * Busca escenarios por nombre, ubicación o tipo.
     *
     * @param request los criterios de búsqueda
     * @return lista de escenarios que coinciden con los criterios
     */
    List<EscenarioResponse> buscarEscenarios(BuscarEscenariosRequest request);

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
	 * Delete a Escenario by id.
	 *
	 * @param id the Address ID
	 */
	void deleteById(Long id);

	/**
	 * Filtra los escenarios según los criterios especificados.
	 *
	 * @param request los criterios de filtrado
	 * @return lista de escenarios filtrados
	 */
	List<EscenarioResponse> filtrarEscenarios(FiltrarEscenariosRequest request);

	/**
	 * Obtiene los detalles completos de un escenario.
	 *
	 * @param id el ID del escenario
	 * @return detalles del escenario
	 */
	DetalleEscenarioResponse obtenerDetalles(Long id);





}
