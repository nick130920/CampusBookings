package edu.usco.campusbookings.application.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DetalleEscenarioResponse {
    private Long id;
    private String nombre;
    private String tipo;
    private String ubicacion;
    private Integer capacidad;
    private String descripcion;
    private String recursos;
    private String imagenUrl;
    private List<String> horariosDisponibles;
}
