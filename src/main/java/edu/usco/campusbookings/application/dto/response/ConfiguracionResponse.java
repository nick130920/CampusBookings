package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.ConfiguracionSistema;

import java.time.LocalDateTime;

public class ConfiguracionResponse {

    private Long id;
    private Integer minDaysAdvance;
    private Integer maxDaysAdvance;
    private String descripcion;
    private ConfiguracionSistema.TipoConfiguracion tipoConfiguracion;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Constructores
    public ConfiguracionResponse() {}

    public ConfiguracionResponse(Long id, Integer minDaysAdvance, Integer maxDaysAdvance, 
                               String descripcion, ConfiguracionSistema.TipoConfiguracion tipoConfiguracion,
                               LocalDateTime updatedAt, String updatedBy) {
        this.id = id;
        this.minDaysAdvance = minDaysAdvance;
        this.maxDaysAdvance = maxDaysAdvance;
        this.descripcion = descripcion;
        this.tipoConfiguracion = tipoConfiguracion;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    // Factory method para crear desde entidad
    public static ConfiguracionResponse fromEntity(ConfiguracionSistema configuracion) {
        return new ConfiguracionResponse(
                configuracion.getId(),
                configuracion.getDiasMinimosAnticipacion(),
                configuracion.getDiasMaximosAnticipacion(),
                configuracion.getDescripcion(),
                configuracion.getTipoConfiguracion(),
                configuracion.getModifiedDate(),
                configuracion.getModifiedBy()
        );
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMinDaysAdvance() {
        return minDaysAdvance;
    }

    public void setMinDaysAdvance(Integer minDaysAdvance) {
        this.minDaysAdvance = minDaysAdvance;
    }

    public Integer getMaxDaysAdvance() {
        return maxDaysAdvance;
    }

    public void setMaxDaysAdvance(Integer maxDaysAdvance) {
        this.maxDaysAdvance = maxDaysAdvance;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ConfiguracionSistema.TipoConfiguracion getTipoConfiguracion() {
        return tipoConfiguracion;
    }

    public void setTipoConfiguracion(ConfiguracionSistema.TipoConfiguracion tipoConfiguracion) {
        this.tipoConfiguracion = tipoConfiguracion;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "ConfiguracionResponse{" +
                "id=" + id +
                ", minDaysAdvance=" + minDaysAdvance +
                ", maxDaysAdvance=" + maxDaysAdvance +
                ", descripcion='" + descripcion + '\'' +
                ", tipoConfiguracion=" + tipoConfiguracion +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}