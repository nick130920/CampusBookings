package edu.usco.campusbookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableJpaAuditing
public class CampusBookingsApplication {

    @PostConstruct
    public void init() {
        // Establecer zona horaria de Colombia para toda la aplicación
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
    }

    public static void main(String[] args) {
        SpringApplication.run(CampusBookingsApplication.class, args);
    }
}
