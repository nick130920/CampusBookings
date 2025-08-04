package edu.usco.campusbookings.infrastructure.config;

import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.domain.model.Ubicacion;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.EstadoReservaJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataEscenarioRepository;
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
public class InitialDataLoader {

    private final SpringDataRolRepository rolRepository;
    private final TipoEscenarioJpaRepository tipoEscenarioRepository;
    private final UbicacionJpaRepository ubicacionRepository;
    private final EstadoReservaJpaRepository estadoReservaRepository;
    private final SpringDataEscenarioRepository escenarioRepository;

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
        
        if (escenarioRepository.count() == 0) {
            log.info("Loading initial USCO scenarios...");
            createEscenariosUSCO();
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
                        .nombre("Laboratorio Especializado")
                        .descripcion("Laboratorio de investigación avanzada")
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
                        .build(),
                TipoEscenario.builder()
                        .nombre("Piscina")
                        .descripcion("Escenario acuático para natación")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Centro Tecnológico")
                        .descripcion("Centro de innovación y tecnología avanzada")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Restaurante")
                        .descripcion("Espacio de alimentación estudiantil")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Cafetería")
                        .descripcion("Espacio de encuentro y alimentación informal")
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

    private void createEscenariosUSCO() {
        // Obtener ubicaciones y tipos existentes
        Ubicacion central = ubicacionRepository.findByNombre("Subsede Central").orElse(null);
        Ubicacion salud = ubicacionRepository.findByNombre("Subsede Salud").orElse(null);
        Ubicacion garzon = ubicacionRepository.findByNombre("Sede Garzón").orElse(null);
        Ubicacion pitalito = ubicacionRepository.findByNombre("Sede Pitalito").orElse(null);
        Ubicacion laPlata = ubicacionRepository.findByNombre("Sede La Plata").orElse(null);

        // Tipos de escenario
        TipoEscenario aula = tipoEscenarioRepository.findByNombre("Aula de Clase").orElse(null);
        TipoEscenario auditorio = tipoEscenarioRepository.findByNombre("Auditorio").orElse(null);
        TipoEscenario labComputacion = tipoEscenarioRepository.findByNombre("Laboratorio de Computación").orElse(null);
        TipoEscenario labCiencias = tipoEscenarioRepository.findByNombre("Laboratorio de Ciencias").orElse(null);
        TipoEscenario labEspecializado = tipoEscenarioRepository.findByNombre("Laboratorio Especializado").orElse(null);
        TipoEscenario cancha = tipoEscenarioRepository.findByNombre("Cancha Deportiva").orElse(null);
        TipoEscenario biblioteca = tipoEscenarioRepository.findByNombre("Biblioteca").orElse(null);
        TipoEscenario piscina = tipoEscenarioRepository.findByNombre("Piscina").orElse(null);
        TipoEscenario centroTecnologico = tipoEscenarioRepository.findByNombre("Centro Tecnológico").orElse(null);
        TipoEscenario restaurante = tipoEscenarioRepository.findByNombre("Restaurante").orElse(null);
        TipoEscenario cafeteria = tipoEscenarioRepository.findByNombre("Cafetería").orElse(null);

        List<Escenario> escenarios = Arrays.asList(
            // === ESCENARIOS DEPORTIVOS ===
            
            // Cancha de Microfútbol
            Escenario.builder()
                .nombre("Cancha de Microfútbol")
                .tipo(cancha)
                .ubicacion(central)
                .capacidad(200)
                .descripcion("Primer escenario deportivo al ingresar por la entrada principal. Superficie de concreto con demarcación certificada por FIFA para fútbol de salón.")
                .recursos("Dos arcos para fútbol sala, agujeros para postes de tenis de campo, pared de concreto para práctica de tenis, graderías")
                .disponible(true)
                .build(),

            // Piscina
            Escenario.builder()
                .nombre("Piscina")
                .tipo(piscina)
                .ubicacion(central)
                .capacidad(50)
                .descripcion("Escenario acuático de 25x10 metros con profundidad variable de 1.20 a 2.10 metros. Cumple normativa Ley 1209 de 2008.")
                .recursos("Seis plataformas de salida, encerramiento con reja, baños y vestiers para hombres y mujeres")
                .disponible(true)
                .build(),

            // Coliseo César Eduardo Medina Perdomo
            Escenario.builder()
                .nombre("Coliseo César Eduardo Medina Perdomo")
                .tipo(cancha)
                .ubicacion(central)
                .capacidad(500)
                .descripcion("Coliseo cubierto de 20.7x31.70 metros con piso portátil tipo NBA certificado por FIBA.")
                .recursos("Piso Robbins All-Star Plus, 2 tableros de vidrio templado, demarcación para baloncesto y voleibol, baños, vestiers y duchas")
                .disponible(true)
                .build(),

            // Campo de Vóley-Playa
            Escenario.builder()
                .nombre("Campo de Vóley-Playa")
                .tipo(cancha)
                .ubicacion(central)
                .capacidad(200)
                .descripcion("Cancha de voleibol playa al aire libre de 34x25 metros con malla permanente disponible 24 horas.")
                .recursos("Malla permanente, graderías, iluminación nocturna")
                .disponible(true)
                .build(),

            // Polideportivo
            Escenario.builder()
                .nombre("Polideportivo")
                .tipo(cancha)
                .ubicacion(central)
                .capacidad(200)
                .descripcion("Escenario techado multideportivo de 28x15 metros con demarcación para múltiples deportes.")
                .recursos("Cancha techada multiuso, demarcación para baloncesto, voleibol, microfútbol y balonmano, graderías")
                .disponible(true)
                .build(),

            // === ESCENARIOS ACADÉMICOS ===

            // Auditorio Olga Tony Vidales
            Escenario.builder()
                .nombre("Auditorio Olga Tony Vidales")
                .tipo(auditorio)
                .ubicacion(central)
                .capacidad(300)
                .descripcion("Auditorio emblemático recientemente renovado con inversión de 92 millones de pesos.")
                .recursos("Nuevo techo interno y externo, sistema eléctrico renovado, tableros electrónicos, sistema de sonido")
                .disponible(true)
                .build(),

            // Nuevo Auditorio Facultad de Economía
            Escenario.builder()
                .nombre("Auditorio Facultad de Economía y Administración")
                .tipo(auditorio)
                .ubicacion(central)
                .capacidad(650)
                .descripcion("El auditorio más moderno y grande del departamento del Huila con inversión de más de 5.000 millones de pesos.")
                .recursos("860m² acondicionamiento acústico, aire acondicionado 87 toneladas, proyector láser, sistema sonido última generación, pantallas grandes")
                .disponible(true)
                .build(),

            // Centro STEM+
            Escenario.builder()
                .nombre("Centro STEM+")
                .tipo(centroTecnologico)
                .ubicacion(central)
                .capacidad(40)
                .descripcion("Centro de última generación para ciencia, tecnología, ingeniería y matemáticas con inversión de 3.570 millones.")
                .recursos("31 computadores especializados en diseño 3D e IA, 20 gafas VR, 2 pantallas táctiles, cámaras 360, sensores, Alexa, conexión metaverso")
                .disponible(true)
                .build(),

            // === LABORATORIOS ESPECIALIZADOS ===

            // Laboratorio de Biodiversidad Molecular
            Escenario.builder()
                .nombre("Laboratorio de Biodiversidad Molecular y Citogenética")
                .tipo(labEspecializado)
                .ubicacion(central)
                .capacidad(15)
                .descripcion("Laboratorio de investigación de última generación para estudios de diversidad genética y análisis de especies.")
                .recursos("Neveras con cámaras y acceso internet, esterilizadores con control digital, centrifugadoras termo-científicas, incubadoras, balanza analítica")
                .disponible(true)
                .build(),

            // Laboratorio de Biología
            Escenario.builder()
                .nombre("Laboratorio de Biología")
                .tipo(labCiencias)
                .ubicacion(central)
                .capacidad(25)
                .descripcion("Laboratorio de ciencias básicas con dotación de 243 millones de pesos.")
                .recursos("Espectrofotómetro, purificador de agua tipo I ultrapura y tipo 3 destilada, equipos para prácticas de biología y bioquímica")
                .disponible(true)
                .build(),

            // Laboratorio de Simulación Científica
            Escenario.builder()
                .nombre("Laboratorio de Simulación Científica")
                .tipo(labComputacion)
                .ubicacion(central)
                .capacidad(20)
                .descripcion("Laboratorio computacional de alta tecnología con inversión de 455 millones de pesos.")
                .recursos("10 estaciones de trabajo configuradas, software especializado simulaciones, fibra óptica, tableros trifásicos, aire acondicionado")
                .disponible(true)
                .build(),

            // === ESPACIOS DE BIENESTAR ===

            // Biblioteca Central
            Escenario.builder()
                .nombre("Biblioteca Central Rafael Cortés Murcia")
                .tipo(biblioteca)
                .ubicacion(central)
                .capacidad(150)
                .descripcion("Biblioteca principal del sistema bibliotecario con horario extendido de lunes a sábado.")
                .recursos("Salas de estudio, internet, bases de datos, colecciones general y especializada, hemeroteca, trabajos de grado")
                .disponible(true)
                .build(),

            // Biblioteca de Salud
            Escenario.builder()
                .nombre("Biblioteca de Salud Gloria Gutiérrez Andrade")
                .tipo(biblioteca)
                .ubicacion(salud)
                .capacidad(80)
                .descripcion("Biblioteca especializada en ciencias de la salud con bases de datos médicas especializadas.")
                .recursos("Bases de datos médicas Ovid y de enfermería Doyma, colección especializada en salud")
                .disponible(true)
                .build(),

            // Restaurante La Venada
            Escenario.builder()
                .nombre("Restaurante La Venada")
                .tipo(restaurante)
                .ubicacion(central)
                .capacidad(600)
                .descripcion("Restaurante principal con capacidad para 260 desayunos, 600 almuerzos y 340 cenas diarias.")
                .recursos("Cocina industrial, comedor amplio, subsidio del 80% por parte de la Universidad, servicio de lunes a domingo")
                .disponible(true)
                .build(),

            // Cafetería Café y Letras
            Escenario.builder()
                .nombre("Cafetería Café y Letras")
                .tipo(cafeteria)
                .ubicacion(central)
                .capacidad(50)
                .descripcion("Espacio de encuentro estudiantil rodeado de árboles con ambiente universitario.")
                .recursos("Mesas sencillas, zona de lectura, ambiente natural, espacio de socialización")
                .disponible(true)
                .build(),

            // === ESPACIOS EN SEDES REGIONALES ===

            // Aulas Sede Garzón
            Escenario.builder()
                .nombre("Nuevo Bloque de Aulas Garzón")
                .tipo(aula)
                .ubicacion(garzon)
                .capacidad(260)
                .descripcion("Edificio de 1.040 metros cuadrados con 8 aulas modernas y capacidad para 260 personas simultáneas.")
                .recursos("8 aulas modernas, escaleras, rampa, ascensor, 6 baterías sanitarias, inversión de 2.541 millones")
                .disponible(true)
                .build(),

            // Biblioteca Sede Pitalito
            Escenario.builder()
                .nombre("Biblioteca Sede Pitalito")
                .tipo(biblioteca)
                .ubicacion(pitalito)
                .capacidad(100)
                .descripcion("Nueva biblioteca de 600 metros cuadrados en dos niveles, avance del 73% en construcción.")
                .recursos("Dos niveles, salas de estudio, área de consulta, colección especializada regional")
                .disponible(true)
                .build(),

            // Laboratorio Sede La Plata
            Escenario.builder()
                .nombre("Laboratorio de Ciencias Básicas La Plata")
                .tipo(labCiencias)
                .ubicacion(laPlata)
                .capacidad(20)
                .descripcion("Laboratorio multidisciplinario del nuevo bloque académico-administrativo entregado en 2024.")
                .recursos("Áreas de química, biología, bioquímica, calidad de aguas, fisiología vegetal, equipos de última generación")
                .disponible(true)
                .build()
        );

        escenarioRepository.saveAll(escenarios);
        log.info("Created {} initial USCO scenarios with real data from university documentation", escenarios.size());
    }
}
