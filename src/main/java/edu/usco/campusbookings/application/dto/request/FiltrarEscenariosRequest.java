package edu.usco.campusbookings.application.dto.request;

import lombok.Data;

@Data
public class FiltrarEscenariosRequest {
    private String tipo;
    private String nombre;
    private String ubicacion;
}
