package edu.usco.campusbookings.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Configuración robusta de zona horaria para toda la aplicación
 */
@Configuration
@Slf4j
public class TimezoneConfig {

    private static final String COLOMBIA_TIMEZONE = "America/Bogota";

    @PostConstruct
    public void init() {
        // Configurar zona horaria del sistema de forma agresiva
        TimeZone colombiaTimeZone = TimeZone.getTimeZone(COLOMBIA_TIMEZONE);
        TimeZone.setDefault(colombiaTimeZone);
        
        // También configurar la propiedad del sistema
        System.setProperty("user.timezone", COLOMBIA_TIMEZONE);
        
        log.info("=== TIMEZONE CONFIGURATION ===");
        log.info("Sistema configurado a zona horaria: {}", TimeZone.getDefault().getID());
        log.info("Offset: {} horas", TimeZone.getDefault().getRawOffset() / (1000 * 60 * 60));
        log.info("ZoneId: {}", ZoneId.systemDefault().getId());
        log.info("==============================");
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configurar zona horaria para Jackson
        mapper.setTimeZone(TimeZone.getTimeZone(COLOMBIA_TIMEZONE));
        
        // Configurar formato de fechas
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
        // Registrar módulo para Java 8 Time
        mapper.registerModule(new JavaTimeModule());
        
        // Configurar para no serializar fechas como timestamps
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        log.info("ObjectMapper configurado con zona horaria: {}", COLOMBIA_TIMEZONE);
        
        return mapper;
    }
}