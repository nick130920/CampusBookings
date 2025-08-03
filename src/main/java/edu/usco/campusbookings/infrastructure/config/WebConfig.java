package edu.usco.campusbookings.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para servir archivos estáticos.
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/escenarios/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Construir ruta absoluta para el directorio completo de uploads
        String absoluteUploadPath = System.getProperty("user.dir") + "/" + uploadDir;
        
        log.info("Configurando manejador de recursos estáticos:");
        log.info("  - Pattern: /uploads/escenarios/**");
        log.info("  - Location: file:{}", absoluteUploadPath);
        log.info("  - Upload dir config: {}", uploadDir);
        
        // Configurar el manejador para servir archivos subidos
        // La URL /uploads/escenarios/** se mapea directamente al directorio de escenarios
        registry.addResourceHandler("/uploads/escenarios/**")
                .addResourceLocations("file:" + absoluteUploadPath)
                .setCachePeriod(3600) // Cache por 1 hora
                .resourceChain(true);
    }
}