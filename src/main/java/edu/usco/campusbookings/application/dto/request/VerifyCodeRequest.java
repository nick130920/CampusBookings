package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyCodeRequest {
    
    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "El código de verificación es requerido")
    @Pattern(regexp = "^\\d{6}$", message = "El código debe tener exactamente 6 dígitos")
    private String code;
}