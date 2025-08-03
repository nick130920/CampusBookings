package edu.usco.campusbookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CampusBookingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusBookingsApplication.class, args);
    }
}
