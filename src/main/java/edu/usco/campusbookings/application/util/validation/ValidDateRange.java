package edu.usco.campusbookings.application.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validación personalizada para validar rangos de fechas al estilo Cal.com.
 * Verifica que:
 * - La fecha de fin sea posterior a la fecha de inicio
 * - La duración esté entre un mínimo y máximo permitido
 * - Las fechas estén en horarios laborales válidos
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    
    String message() default "Rango de fechas inválido";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Nombre del campo que contiene la fecha de inicio
     */
    String startField();
    
    /**
     * Nombre del campo que contiene la fecha de fin
     */
    String endField();
    
    /**
     * Duración mínima en minutos (por defecto 30 minutos)
     */
    long minDurationMinutes() default 30;
    
    /**
     * Duración máxima en horas (por defecto 4 horas, consistente con el servicio)
     */
    long maxDurationHours() default 4;
    
    /**
     * Hora mínima permitida para reservas (por defecto 8:00 AM)
     */
    int minHour() default 8;
    
    /**
     * Hora máxima permitida para reservas (por defecto 8:00 PM)
     */
    int maxHour() default 20;
}