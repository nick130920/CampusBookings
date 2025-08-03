package edu.usco.campusbookings.application.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utilidad para validación y análisis de fortaleza de contraseñas.
 */
public class PasswordValidationUtil {

    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@$!%*?&].*");

    /**
     * Valida la fortaleza de una contraseña y retorna información detallada.
     *
     * @param password la contraseña a validar
     * @return mapa con información de validación
     */
    public static Map<String, Object> validatePasswordStrength(String password) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // Validaciones básicas
        boolean hasMinLength = password != null && password.length() >= 8;
        boolean hasMaxLength = password != null && password.length() <= 100;
        boolean hasLowercase = password != null && LOWERCASE_PATTERN.matcher(password).matches();
        boolean hasUppercase = password != null && UPPERCASE_PATTERN.matcher(password).matches();
        boolean hasDigit = password != null && DIGIT_PATTERN.matcher(password).matches();
        boolean hasSpecialChar = password != null && SPECIAL_CHAR_PATTERN.matcher(password).matches();

        // Calcular puntuación de fortaleza (0-100)
        int score = 0;
        if (hasMinLength) score += 20;
        if (hasLowercase) score += 15;
        if (hasUppercase) score += 15;
        if (hasDigit) score += 15;
        if (hasSpecialChar) score += 15;
        
        // Bonificaciones por longitud
        if (password != null) {
            if (password.length() >= 12) score += 10;
            if (password.length() >= 16) score += 10;
        }

        // Generar errores y sugerencias
        if (!hasMinLength) {
            errors.add("La contraseña debe tener al menos 8 caracteres");
            suggestions.add("Usa al menos 8 caracteres");
        }
        
        if (!hasMaxLength) {
            errors.add("La contraseña no puede tener más de 100 caracteres");
        }

        if (!hasLowercase) {
            errors.add("Debe contener al menos una letra minúscula");
            suggestions.add("Agrega una letra minúscula (a-z)");
        }

        if (!hasUppercase) {
            errors.add("Debe contener al menos una letra mayúscula");
            suggestions.add("Agrega una letra mayúscula (A-Z)");
        }

        if (!hasDigit) {
            errors.add("Debe contener al menos un número");
            suggestions.add("Agrega un número (0-9)");
        }

        if (!hasSpecialChar) {
            errors.add("Debe contener al menos un carácter especial (@$!%*?&)");
            suggestions.add("Agrega un carácter especial (@$!%*?&)");
        }

        // Determinar nivel de fortaleza
        String strength;
        if (score < 40) {
            strength = "MUY_DEBIL";
        } else if (score < 60) {
            strength = "DEBIL";
        } else if (score < 80) {
            strength = "MODERADA";
        } else if (score < 90) {
            strength = "FUERTE";
        } else {
            strength = "MUY_FUERTE";
        }

        boolean isValid = errors.isEmpty();

        result.put("valid", isValid);
        result.put("score", score);
        result.put("strength", strength);
        result.put("errors", errors);
        result.put("suggestions", suggestions);
        result.put("requirements", Map.of(
            "minLength", hasMinLength,
            "hasLowercase", hasLowercase,
            "hasUppercase", hasUppercase,
            "hasDigit", hasDigit,
            "hasSpecialChar", hasSpecialChar
        ));

        return result;
    }

    /**
     * Valida si una contraseña cumple con los requisitos mínimos.
     *
     * @param password la contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValidPassword(String password) {
        Map<String, Object> validation = validatePasswordStrength(password);
        return (Boolean) validation.get("valid");
    }
}