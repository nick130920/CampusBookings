package edu.usco.campusbookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class CampusBookingsApplication {

    public static void main(String[] args) {
        // Configurar zona horaria antes de iniciar la aplicación
        System.setProperty("user.timezone", "America/Bogota");
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        
        SpringApplication.run(CampusBookingsApplication.class, args);
    }
}
