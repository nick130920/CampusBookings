package edu.usco.campusbookings.application.dto.request;

import lombok.Data;

@Data
public class BuscarEscenariosRequest {
    private String nombre;
    private String tipo;
    private String edificio;
    private String ubicacion;
    private Integer capacidad;
    private Boolean disponible;
}