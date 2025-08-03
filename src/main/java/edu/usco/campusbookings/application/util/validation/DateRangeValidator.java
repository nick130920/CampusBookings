package edu.usco.campusbookings.application.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Validador para rangos de fechas inspirado en las validaciones de Cal.com.
 * Implementa validaciones robustas para reservas de escenarios.
 */
@Slf4j
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startFieldName;
    private String endFieldName;
    private long minDurationMinutes;
    private long maxDurationHours;
    private int minHour;
    private int maxHour;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startFieldName = constraintAnnotation.startField();
        this.endFieldName = constraintAnnotation.endField();
        this.minDurationMinutes = constraintAnnotation.minDurationMinutes();
        this.maxDurationHours = constraintAnnotation.maxDurationHours();
        this.minHour = constraintAnnotation.minHour();
        this.maxHour = constraintAnnotation.maxHour();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            LocalDateTime startDate = getFieldValue(value, startFieldName);
            LocalDateTime endDate = getFieldValue(value, endFieldName);

            // Si alguna fecha es null, dejamos que las otras validaciones se encarguen
            if (startDate == null || endDate == null) {
                return true;
            }

            // Limpiar mensajes de error por defecto
            context.disableDefaultConstraintViolation();

            // Validar que la fecha de fin sea posterior a la de inicio
            if (!endDate.isAfter(startDate)) {
                addConstraintViolation(context, "La fecha de fin debe ser posterior a la fecha de inicio");
                return false;
            }

            // Validar duración mínima
            Duration duration = Duration.between(startDate, endDate);
            if (duration.toMinutes() < minDurationMinutes) {
                addConstraintViolation(context, 
                    String.format("La reserva debe tener una duración mínima de %d minutos", minDurationMinutes));
                return false;
            }

            // Validar duración máxima
            if (duration.toHours() > maxDurationHours) {
                addConstraintViolation(context, 
                    String.format("La reserva no puede exceder %d horas de duración", maxDurationHours));
                return false;
            }

            // Validar horarios permitidos
            if (!isWithinAllowedHours(startDate) || !isWithinAllowedHours(endDate)) {
                addConstraintViolation(context, 
                    String.format("Las reservas solo se pueden realizar entre las %d:00 y las %d:00", minHour, maxHour));
                return false;
            }

            // Validar que no sea en fin de semana (opcional, se puede activar según las reglas de negocio)
            // if (isWeekend(startDate) || isWeekend(endDate)) {
            //     addConstraintViolation(context, "No se permiten reservas en fines de semana");
            //     return false;
            // }

            return true;

        } catch (Exception e) {
            log.error("Error al validar rango de fechas", e);
            return false;
        }
    }

    private LocalDateTime getFieldValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (LocalDateTime) field.get(object);
    }

    private boolean isWithinAllowedHours(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        return hour >= minHour && hour <= maxHour;
    }

    @SuppressWarnings("unused")
    private boolean isWeekend(LocalDateTime dateTime) {
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Sábado = 6, Domingo = 7
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}