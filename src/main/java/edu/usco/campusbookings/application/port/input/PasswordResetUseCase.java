package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ForgotPasswordRequest;
import edu.usco.campusbookings.application.dto.request.ResetPasswordRequest;
import edu.usco.campusbookings.application.dto.request.VerifyCodeRequest;
import edu.usco.campusbookings.application.dto.response.VerifyCodeResponse;

public interface PasswordResetUseCase {
    
    /**
     * Envía un código de verificación al email del usuario
     * @param request Request con el email del usuario
     * @return Mensaje de confirmación
     */
    String sendPasswordResetCode(ForgotPasswordRequest request);
    
    /**
     * Verifica el código de verificación y genera un token temporal
     * @param request Request con email y código
     * @return Response con el token temporal
     */
    VerifyCodeResponse verifyPasswordResetCode(VerifyCodeRequest request);
    
    /**
     * Cambia la contraseña del usuario usando el token temporal
     * @param request Request con token y nueva contraseña
     * @return Mensaje de confirmación
     */
    String resetPassword(ResetPasswordRequest request);
}