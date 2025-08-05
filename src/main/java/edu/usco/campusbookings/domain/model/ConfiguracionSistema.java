package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "configuracion_sistema")
public class ConfiguracionSistema extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(value = 0, message = "Los días mínimos no pueden ser negativos")
    @Max(value = 7, message = "Los días mínimos no pueden ser más de 7")
    @Column(name = "dias_minimos_anticipacion", nullable = false)
    private Integer diasMinimosAnticipacion;

    @NotNull
    @Min(value = 7, message = "Los días máximos deben ser al menos 7")
    @Max(value = 365, message = "Los días máximos no pueden ser más de 365")
    @Column(name = "dias_maximos_anticipacion", nullable = false)
    private Integer diasMaximosAnticipacion;

    @Column(name = "tipo_configuracion", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoConfiguracion tipoConfiguracion;

    @Column(name = "descripcion")
    private String descripcion;

    // Constructores
    public ConfiguracionSistema() {}

    public ConfiguracionSistema(Integer diasMinimosAnticipacion, Integer diasMaximosAnticipacion, 
                               TipoConfiguracion tipoConfiguracion, String descripcion) {
        this.diasMinimosAnticipacion = diasMinimosAnticipacion;
        this.diasMaximosAnticipacion = diasMaximosAnticipacion;
        this.tipoConfiguracion = tipoConfiguracion;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TipoConfiguracion getTipoConfiguracion() {
        return tipoConfiguracion;
    }

    public void setTipoConfiguracion(TipoConfiguracion tipoConfiguracion) {
        this.tipoConfiguracion = tipoConfiguracion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Enum para tipos de configuración
    public enum TipoConfiguracion {
        RESERVAS,
        NOTIFICACIONES,
        GENERAL
    }

    // Método de validación personalizada
    @PostLoad
    @PrePersist
    @PreUpdate
    private void validarRangos() {
        if (diasMinimosAnticipacion != null && diasMaximosAnticipacion != null) {
            if (diasMinimosAnticipacion >= diasMaximosAnticipacion) {
                throw new IllegalArgumentException("Los días mínimos deben ser menores que los días máximos");
            }
        }
    }

    @Override
    public String toString() {
        return "ConfiguracionSistema{" +
                "id=" + id +
                ", diasMinimosAnticipacion=" + diasMinimosAnticipacion +
                ", diasMaximosAnticipacion=" + diasMaximosAnticipacion +
                ", tipoConfiguracion=" + tipoConfiguracion +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}