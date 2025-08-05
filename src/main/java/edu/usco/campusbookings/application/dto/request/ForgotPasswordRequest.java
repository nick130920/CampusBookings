package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    
    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;
}