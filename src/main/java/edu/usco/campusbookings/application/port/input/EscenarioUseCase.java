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
     * Creates a new escenario.
     *
     * @param escenarioRequest the escenario request input
     * @return the created escenario response
     */
    EscenarioResponse createEscenario(EscenarioRequest escenarioRequest);

    List<EscenarioResponse> createEscenarios(List<EscenarioRequest> escenarioRequests);

    /**
     * Updates an existing escenario.
     *
     * @param id the escenario ID
     * @param request the escenario request
     * @return the updated escenario response
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
     * Get all unique scenario types.
     *
     * @return list of unique scenario types
     */
    List<String> getTiposEscenario();

    /**
     * Get all unique scenario locations.
     *
     * @return list of unique scenario locations
     */
    List<String> getUbicaciones();

    /**
     * Obtiene los detalles completos de un escenario.
     *
     * @param id el ID del escenario
     * @return detalles del escenario
     */
    DetalleEscenarioResponse obtenerDetalles(Long id);
}
