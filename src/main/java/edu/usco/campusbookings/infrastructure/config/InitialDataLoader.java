package edu.usco.campusbookings.infrastructure.config;

import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.domain.model.Ubicacion;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.EstadoReservaJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.TipoEscenarioJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.UbicacionJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataRolRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class to load initial data into the database.
 * This will only run when the 'dev' or 'local' profile is active.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"dev", "local", "default"})
public class InitialDataLoader {

    private final SpringDataRolRepository rolRepository;
    private final TipoEscenarioJpaRepository tipoEscenarioRepository;
    private final UbicacionJpaRepository ubicacionRepository;
    private final EstadoReservaJpaRepository estadoReservaRepository;

    @PostConstruct
    @Transactional
    public void loadInitialData() {
        if (rolRepository.count() == 0) {
            log.info("Loading initial roles...");
            createRoles();
        }

        if (tipoEscenarioRepository.count() == 0) {
            log.info("Loading initial scenario types...");
            createTipoEscenarios();
        }

        if (ubicacionRepository.count() == 0) {
            log.info("Loading initial locations...");
            createUbicaciones();
        }
        
        if (estadoReservaRepository.count() == 0) {
            log.info("Loading initial reservation states...");
            createEstadosReserva();
        }
    }

    private void createRoles() {
        Rol admin = Rol.builder()
                .nombre("ADMIN")
                .build();

        Rol usuario = Rol.builder()
                .nombre("USUARIO")
                .build();

        rolRepository.saveAll(List.of(admin, usuario));
        log.info("Created initial roles: ADMIN, USUARIO");
    }

    private void createTipoEscenarios() {
        List<TipoEscenario> tipos = Arrays.asList(
                TipoEscenario.builder()
                        .nombre("Aula de Clase")
                        .descripcion("Aula estándar para clases teóricas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Laboratorio de Computación")
                        .descripcion("Laboratorio equipado con computadores")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Laboratorio de Ciencias")
                        .descripcion("Laboratorio para prácticas de ciencias básicas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Auditorio")
                        .descripcion("Espacio para eventos y presentaciones")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Sala de Reuniones")
                        .descripcion("Espacio para reuniones y trabajo en grupo")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Cancha Deportiva")
                        .descripcion("Espacio para actividades deportivas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Biblioteca")
                        .descripcion("Espacio de estudio y consulta")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Gimnasio")
                        .descripcion("Espacio para actividades físicas")
                        .build()
        );

        tipoEscenarioRepository.saveAll(tipos);
        log.info("Created {} initial scenario types", tipos.size());
    }

    private void createUbicaciones() {
        // Sede Principal Neiva - Subsede Central
        Ubicacion central = Ubicacion.builder()
                .nombre("Subsede Central")
                .direccion("Avenida Pastrana Borrero - Carrera 1")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Sede Principal Neiva - Torre Administrativa y Postgrados
        Ubicacion torreAdmin = Ubicacion.builder()
                .nombre("Torre Administrativa y Postgrados")
                .direccion("Carrera 5 No. 23-40")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Sede Principal Neiva - Subsede Salud
        Ubicacion salud = Ubicacion.builder()
                .nombre("Subsede Salud")
                .direccion("Calle 9 # 14-03")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Sede Garzón
        Ubicacion garzon = Ubicacion.builder()
                .nombre("Sede Garzón")
                .direccion("Vereda Las Termitas")
                .ciudad("Garzón")
                .pais("Colombia")
                .build();

        // Sede Pitalito
        Ubicacion pitalito = Ubicacion.builder()
                .nombre("Sede Pitalito")
                .direccion("Kilómetro 1 vía Vereda El Macal")
                .ciudad("Pitalito")
                .pais("Colombia")
                .build();

        // Sede La Plata
        Ubicacion laPlata = Ubicacion.builder()
                .nombre("Sede La Plata")
                .direccion("Kilómetro 1 vía a Fátima")
                .ciudad("La Plata")
                .pais("Colombia")
                .build();

        List<Ubicacion> ubicaciones = List.of(central, torreAdmin, salud, garzon, pitalito, laPlata);
        ubicacionRepository.saveAll(ubicaciones);
        log.info("Created {} initial locations", ubicaciones.size());
    }

    private void createEstadosReserva() {
        EstadoReserva pendiente = EstadoReserva.builder()
                .nombre("PENDIENTE")
                .build();

        EstadoReserva aprobada = EstadoReserva.builder()
                .nombre("APROBADA")
                .build();

        EstadoReserva rechazada = EstadoReserva.builder()
                .nombre("RECHAZADA")
                .build();

        EstadoReserva cancelada = EstadoReserva.builder()
                .nombre("CANCELADA")
                .build();

        List<EstadoReserva> estados = List.of(pendiente, aprobada, rechazada, cancelada);
        estadoReservaRepository.saveAll(estados);
        log.info("Created {} initial reservation states: PENDIENTE, APROBADA, RECHAZADA, CANCELADA", estados.size());
    }
}
