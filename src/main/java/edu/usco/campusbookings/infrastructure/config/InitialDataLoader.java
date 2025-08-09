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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class to load initial data into the database.
 * DISABLED: Conflictúa con DataInitializer - usando solo DataInitializer
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
//@Component // DISABLED - Conflicto con DataInitializer
public class InitialDataLoader {

    private final SpringDataRolRepository rolRepository;
    private final TipoEscenarioJpaRepository tipoEscenarioRepository;
    private final UbicacionJpaRepository ubicacionRepository;
    private final EstadoReservaJpaRepository estadoReservaRepository;
    private final SpringDataEscenarioRepository escenarioRepository;

    // DISABLED - Usando DataInitializer en su lugar
    // @PostConstruct
    // @Transactional
    public void loadInitialData_DISABLED() {
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
                        .nombre("Deportivo")
                        .descripcion("Espacios destinados a actividades deportivas y recreativas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Auditorio")
                        .descripcion("Espacios para eventos, conferencias y presentaciones")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Laboratorio/Didáctico")
                        .descripcion("Espacios de aprendizaje práctico y experimentación")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Laboratorio")
                        .descripcion("Laboratorios especializados para investigación y práctica")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Biblioteca")
                        .descripcion("Espacios de estudio, consulta y recursos bibliográficos")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Sala de Cómputo")
                        .descripcion("Salas equipadas con computadores y tecnología")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Restaurante")
                        .descripcion("Espacios de alimentación estudiantil y universitaria")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Cafetería")
                        .descripcion("Espacios de encuentro y alimentación informal")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Administrativo")
                        .descripcion("Espacios destinados a actividades administrativas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Bloque Académico")
                        .descripcion("Conjuntos de aulas para clases y actividades académicas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Edificio Académico")
                        .descripcion("Edificios completos destinados a actividades académicas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Investigación/Práctica")
                        .descripcion("Espacios destinados a investigación y prácticas especializadas")
                        .build()
        );

        tipoEscenarioRepository.saveAll(tipos);
        log.info("Created {} initial scenario types", tipos.size());
    }

    private void createUbicaciones() {
        // Sede Central
        Ubicacion sedeCentral = Ubicacion.builder()
                .nombre("Sede Central")
                .direccion("Avenida Pastrana Borrero - Carrera 1")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Subsede Salud
        Ubicacion subsedeSalud = Ubicacion.builder()
                .nombre("Subsede Salud")
                .direccion("Calle 9 # 14-03")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Sede Neiva Centro (Torre Administrativa)
        Ubicacion sedeNeivaCentro = Ubicacion.builder()
                .nombre("Sede Neiva Centro")
                .direccion("Carrera 5 No. 23-40")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Sede Garzón
        Ubicacion sedeGarzon = Ubicacion.builder()
                .nombre("Sede Garzón")
                .direccion("Vereda Las Termitas")
                .ciudad("Garzón")
                .pais("Colombia")
                .build();

        // Sede Pitalito
        Ubicacion sedePitalito = Ubicacion.builder()
                .nombre("Sede Pitalito")
                .direccion("Kilómetro 1 vía Vereda El Macal")
                .ciudad("Pitalito")
                .pais("Colombia")
                .build();

        // Sede La Plata
        Ubicacion sedeLaPlata = Ubicacion.builder()
                .nombre("Sede La Plata")
                .direccion("Kilómetro 1 vía a Fátima")
                .ciudad("La Plata")
                .pais("Colombia")
                .build();

        List<Ubicacion> ubicaciones = List.of(sedeCentral, subsedeSalud, sedeNeivaCentro, sedeGarzon, sedePitalito, sedeLaPlata);
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
        // Obtener ubicaciones
        Ubicacion sedeCentral = ubicacionRepository.findByNombre("Sede Central").orElse(null);
        Ubicacion subsedeSalud = ubicacionRepository.findByNombre("Subsede Salud").orElse(null);
        Ubicacion sedeNeivaCentro = ubicacionRepository.findByNombre("Sede Neiva Centro").orElse(null);
        Ubicacion sedeGarzon = ubicacionRepository.findByNombre("Sede Garzón").orElse(null);
        Ubicacion sedePitalito = ubicacionRepository.findByNombre("Sede Pitalito").orElse(null);
        Ubicacion sedeLaPlata = ubicacionRepository.findByNombre("Sede La Plata").orElse(null);

        // Obtener tipos de escenario
        TipoEscenario deportivo = tipoEscenarioRepository.findByNombre("Deportivo").orElse(null);
        TipoEscenario auditorio = tipoEscenarioRepository.findByNombre("Auditorio").orElse(null);
        TipoEscenario laboratorioDida = tipoEscenarioRepository.findByNombre("Laboratorio/Didáctico").orElse(null);
        TipoEscenario laboratorio = tipoEscenarioRepository.findByNombre("Laboratorio").orElse(null);
        TipoEscenario biblioteca = tipoEscenarioRepository.findByNombre("Biblioteca").orElse(null);
        TipoEscenario salaComputo = tipoEscenarioRepository.findByNombre("Sala de Cómputo").orElse(null);
        TipoEscenario restaurante = tipoEscenarioRepository.findByNombre("Restaurante").orElse(null);
        TipoEscenario cafeteria = tipoEscenarioRepository.findByNombre("Cafetería").orElse(null);
        TipoEscenario administrativo = tipoEscenarioRepository.findByNombre("Administrativo").orElse(null);
        TipoEscenario bloqueAcademico = tipoEscenarioRepository.findByNombre("Bloque Académico").orElse(null);
        TipoEscenario edificioAcademico = tipoEscenarioRepository.findByNombre("Edificio Académico").orElse(null);
        TipoEscenario investigacionPractica = tipoEscenarioRepository.findByNombre("Investigación/Práctica").orElse(null);

        List<Escenario> escenarios = Arrays.asList(
            // Escenarios basados en el CSV oficial de la USCO

            // Escenarios Deportivos
            Escenario.builder()
                .nombre("Cancha de Microfútbol")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(200)
                .descripcion("Cancha de concreto 40x20m con graderías; arcos fútbol sala; pared tenis; normativa FIFA.")
                .recursos("Área deportiva, cancha de concreto, graderías, arcos de fútbol sala")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Piscina")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(50) // No especificada en CSV
                .descripcion("Piscina semiolímpica 25x10m; profundidad 1.2-2.1m; seis poyetes; baños y vestiers.")
                .recursos("Área deportiva, piscina semiolímpica, baños, vestiers")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Coliseo César Eduardo Medina Perdomo")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(500)
                .descripcion("Coliseo cubierto 20.7x31.7m; piso NBA Robbins; tableros vidrio; demarcación baloncesto y voleibol.")
                .recursos("Bloque 21, coliseo cubierto, piso NBA, tableros de vidrio")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Cancha de Vóley-Playa")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(200)
                .descripcion("Cancha 34x25m de arena; malla permanente; graderías.")
                .recursos("Área deportiva, cancha de arena, malla permanente, graderías")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Polideportivo")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(200)
                .descripcion("Escenario techado 28x15m multipropósito; baloncesto, voleibol, microfútbol.")
                .recursos("Área deportiva, escenario techado, multipropósito")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Campo de Fútbol")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Campo profesional de césped; rodeado por pista atlética.")
                .recursos("Área deportiva, campo de césped, pista atlética")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Pista Atlética")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Pista 400m; 6 carriles de arena de arcilla; placas marcadoras cada 100m.")
                .recursos("Área deportiva, pista de 400m, 6 carriles")
                .disponible(true)
                .build(),

            // Auditorios
            Escenario.builder()
                .nombre("Auditorio Olga Tony Vidales")
                .tipo(auditorio)
                .ubicacion(sedeCentral)
                .capacidad(300)
                .descripcion("Auditorio renovado; sistema audiovisual moderno; nuevo techo y electricidad.")
                .recursos("Bloque 02, sistema audiovisual, techo renovado")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Auditorio Facultad Economía y Administración")
                .tipo(auditorio)
                .ubicacion(sedeCentral)
                .capacidad(700)
                .descripcion("Auditorio 860m2 acústica; 260m2 aislamiento; aire 87Tn; proyector láser; sonido e iluminación profesional.")
                .recursos("Bloque 30, acústica profesional, aire acondicionado, proyector láser")
                .disponible(true)
                .build(),

            // Laboratorios y Centros Especializados
            Escenario.builder()
                .nombre("Centro STEM+")
                .tipo(laboratorioDida)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("31 PCs diseño 3D; 20 gafas VR; pantallas táctiles; cámaras 360; IA; metaverso.")
                .recursos("Bloque 30, 31 PCs especializados, 20 gafas VR, pantallas táctiles")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Biodiversidad Molecular y Citogenética")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Neveras conectadas; esterilizadores digitales; centrifugadoras; incubadoras; balanza analítica.")
                .recursos("Bloque 10, neveras especializadas, esterilizadores, centrifugadoras")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Biología")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(30)
                .descripcion("Dotación 243M COP; espectrofotómetro; purificador agua Tipo I/III.")
                .recursos("Bloque 10, espectrofotómetro, purificador de agua")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Química")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(30)
                .descripcion("Dotación 51M COP; bomba vacío; pH-metro digital; reactivos especializados.")
                .recursos("Bloque 10, bomba de vacío, pH-metro digital, reactivos")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Microbiología")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(30)
                .descripcion("Dotación 104M COP; incubadora; colorímetro; autoclave; bacto-incinerador.")
                .recursos("Bloque 10, incubadora, colorímetro, autoclave")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Simulación Científica")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(10)
                .descripcion("455M COP; 10 estaciones de trabajo; software simulaciones; aire acondicionado.")
                .recursos("Bloque 10, 10 estaciones de trabajo, software especializado")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Multimedia")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("334M COP; producción audiovisual y multimedia.")
                .recursos("Bloque 10, equipos de producción audiovisual")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Arqueología")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("160M COP; análisis arqueológicos; investigación patrimonial.")
                .recursos("Bloque 10, equipos de análisis arqueológico")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Suelos (LABGAA)")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Análisis físico-químico de suelos; planes de fertilización.")
                .recursos("Bloque 27, equipos análisis de suelos")
                .disponible(true)
                .build(),

            // Bibliotecas
            Escenario.builder()
                .nombre("Biblioteca Central Rafael Cortés Murcia")
                .tipo(biblioteca)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Sala general; hemeroteca; sala virtual Ecopetrol; bases de datos.")
                .recursos("Biblioteca, sala general, hemeroteca, bases de datos")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Biblioteca Salud Gloria Gutiérrez Andrade")
                .tipo(biblioteca)
                .ubicacion(subsedeSalud)
                .capacidad(null) // No especificada en CSV
                .descripcion("Biblioteca especializada en ciencias de la salud; bases de datos médicas.")
                .recursos("Bloque 51, bases de datos médicas especializadas")
                .disponible(true)
                .build(),

            // Salas de Cómputo
            Escenario.builder()
                .nombre("Sala Virtual Ecopetrol")
                .tipo(salaComputo)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Sala de PCs; conexión a bases de datos; horario extendido.")
                .recursos("Biblioteca Central, PCs, bases de datos")
                .disponible(true)
                .build(),

            // Restaurantes y Cafeterías
            Escenario.builder()
                .nombre("Restaurante La Venada")
                .tipo(restaurante)
                .ubicacion(sedeCentral)
                .capacidad(600)
                .descripcion("Servicio subvencionado: 260 desayunos; 600 almuerzos; 340 cenas diarias.")
                .recursos("Bloque 20, cocina industrial, comedor")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Restaurante Facultad Salud")
                .tipo(restaurante)
                .ubicacion(subsedeSalud)
                .capacidad(180)
                .descripcion("Restaurante: 100 desayunos; 180 almuerzos; 80 cenas diarias.")
                .recursos("Bloque 52, cocina, comedor")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Cafetería Café y Letras")
                .tipo(cafeteria)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Cafetería al aire libre; zona de lectura y socialización.")
                .recursos("Bloque 15, zona al aire libre, área de lectura")
                .disponible(true)
                .build(),

            // Espacios Administrativos
            Escenario.builder()
                .nombre("Torre Administrativa")
                .tipo(administrativo)
                .ubicacion(sedeNeivaCentro)
                .capacidad(null) // No especificada en CSV
                .descripcion("Rectoría; Vicerrectorías; Secretaría; restaurante y fotocopiado.")
                .recursos("TAD, oficinas administrativas, restaurante")
                .disponible(true)
                .build(),

            // Bloques Académicos - Sede Central
            Escenario.builder()
                .nombre("Bloque 09 Aulas UNO")
                .tipo(bloqueAcademico)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Conjunto de aulas A-09-XXX; capacidades variables.")
                .recursos("Bloque 09, aulas múltiples")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Bloque 14 Aulas DOS")
                .tipo(bloqueAcademico)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Conjunto de aulas A-14-XXX; clases generales.")
                .recursos("Bloque 14, aulas generales")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Bloque 16 Aulas TRES")
                .tipo(bloqueAcademico)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Conjunto de aulas A-16-XXX.")
                .recursos("Bloque 16, aulas")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Bloque 17 Aulas CUATRO")
                .tipo(bloqueAcademico)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Conjunto de aulas A-17-XXX.")
                .recursos("Bloque 17, aulas")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Bloque 18 Aulas CINCO")
                .tipo(bloqueAcademico)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Conjunto de aulas A-18-XXX.")
                .recursos("Bloque 18, aulas")
                .disponible(true)
                .build(),

            // Edificios Académicos
            Escenario.builder()
                .nombre("Edificio Facultad Educación e ILEUSCO")
                .tipo(edificioAcademico)
                .ubicacion(sedeCentral)
                .capacidad(null) // No especificada en CSV
                .descripcion("Dos bloques; 5 pisos; 44 aulas; auditorio; biblioteca; laboratorios.")
                .recursos("Nuevo Edificio Educación, 44 aulas, auditorio, biblioteca")
                .disponible(true)
                .build(),

            // Sedes Regionales
            Escenario.builder()
                .nombre("Bloque 01 GAR Aulas")
                .tipo(edificioAcademico)
                .ubicacion(sedeGarzon)
                .capacidad(260)
                .descripcion("Nuevo bloque 1,040m²; 8 aulas; rampas y ascensor.")
                .recursos("Garzón Bloque Aulas, 8 aulas, accesibilidad")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Bloque 02 PIT Aulas")
                .tipo(edificioAcademico)
                .ubicacion(sedePitalito)
                .capacidad(null) // No especificada en CSV
                .descripcion("19 aulas; salas audiovisuales; sistemas; auditorios.")
                .recursos("Pitalito Bloque 02, 19 aulas, salas audiovisuales")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Biblioteca Pitalito (en construcción)")
                .tipo(biblioteca)
                .ubicacion(sedePitalito)
                .capacidad(null) // No especificada en CSV
                .descripcion("600m²; dos niveles; avance 73%.")
                .recursos("Pitalito Biblioteca, dos niveles")
                .disponible(false) // En construcción
                .build(),

            Escenario.builder()
                .nombre("Granja Experimental Ecológica")
                .tipo(investigacionPractica)
                .ubicacion(sedeLaPlata)
                .capacidad(null) // No especificada en CSV
                .descripcion("Espacio agropecuario para prácticas y experimentación.")
                .recursos("Sede La Plata, espacio agropecuario")
                .disponible(true)
                .build()
        );

        escenarioRepository.saveAll(escenarios);
        log.info("Created {} initial USCO scenarios with real data from university documentation", escenarios.size());
    }
}
