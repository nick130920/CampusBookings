package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ActualizarConfiguracionRequest {

    @NotNull(message = "Los días mínimos de anticipación son requeridos")
    @Min(value = 0, message = "Los días mínimos no pueden ser negativos")
    @Max(value = 7, message = "Los días mínimos no pueden ser más de 7")
    private Integer diasMinimosAnticipacion;

    @NotNull(message = "Los días máximos de anticipación son requeridos")
    @Min(value = 7, message = "Los días máximos deben ser al menos 7")
    @Max(value = 365, message = "Los días máximos no pueden ser más de 365")
    private Integer diasMaximosAnticipacion;

    private String descripcion;

    // Constructores
    public ActualizarConfiguracionRequest() {}

    public ActualizarConfiguracionRequest(Integer diasMinimosAnticipacion, 
                                        Integer diasMaximosAnticipacion, 
                                        String descripcion) {
        this.diasMinimosAnticipacion = diasMinimosAnticipacion;
        this.diasMaximosAnticipacion = diasMaximosAnticipacion;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Integer getDiasMinimosAnticipacion() {
        return diasMinimosAnticipacion;
    }

    public void setDiasMinimosAnticipacion(Integer diasMinimosAnticipacion) {
        this.diasMinimosAnticipacion = diasMinimosAnticipacion;
    }

    public Integer getDiasMaximosAnticipacion() {
        return diasMaximosAnticipacion;
    }

    public void setDiasMaximosAnticipacion(Integer diasMaximosAnticipacion) {
        this.diasMaximosAnticipacion = diasMaximosAnticipacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Validación personalizada
    public boolean isValid() {
        return diasMinimosAnticipacion != null && 
               diasMaximosAnticipacion != null && 
               diasMinimosAnticipacion < diasMaximosAnticipacion;
    }

    @Override
    public String toString() {
        return "ActualizarConfiguracionRequest{" +
                "diasMinimosAnticipacion=" + diasMinimosAnticipacion +
                ", diasMaximosAnticipacion=" + diasMaximosAnticipacion +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}