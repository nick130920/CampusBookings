package edu.usco.campusbookings.infrastructure.adapter.input.controller;


import edu.usco.campusbookings.application.port.input.EscenarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Integration tests for {@link EscenarioRestController} focusing on scenario types and locations endpoints.
 */
@ExtendWith(MockitoExtension.class)
@WithMockUser(roles = "ADMIN")
class TipoUbicacionControllerIntegrationTest {

    @Mock
    private EscenarioUseCase escenarioUseCase;

    @InjectMocks
    private EscenarioRestController escenarioController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(escenarioController).build();
    }

    // Test data
    private List<String> getSampleTiposEscenario() {
        return Arrays.asList(
            "Aula de Clase",
            "Laboratorio de Computación",
            "Auditorio",
            "Sala de Reuniones",
            "Cancha Deportiva",
            "Teatro",
            "Sala de Conferencias",
            "Laboratorio de Ciencias",
            "Biblioteca",
            "Sala de Estudio",
            "Taller",
            "Gimnasio",
            "Sala de Profesores",
            "Cafetería",
            "Oficina Administrativa"
        );
    }

    private List<String> getSampleUbicaciones() {
        return Arrays.asList(
            // Sede Central
            "Edificio de Ingeniería - Piso 1",
            "Edificio de Ingeniería - Piso 2",
            "Edificio de Ingeniería - Piso 3",
            "Edificio de Ciencias Básicas",
            "Edificio de Ciencias de la Salud",
            "Edificio de Ciencias Agrarias",
            "Edificio de Ciencias Económicas",
            "Edificio de Ciencias de la Educación",
            "Biblioteca Central",
            "Auditorio Principal",
            "Bloque Administrativo",
            "Comedor Universitario",
            "Zona Deportiva - Canchas Múltiples",
            "Zona Deportiva - Coliseo",
            "Zona Deportiva - Piscina",
            
            // Sede Pitalito
            "Edificio Principal - Pitalito",
            "Bloque A - Pitalito",
            "Bloque B - Pitalito",
            "Laboratorios - Pitalito",
            "Zona Deportiva - Pitalito",
            
            // Sede Garzón
            "Edificio Principal - Garzón",
            "Aulas - Garzón",
            "Laboratorios - Garzón",
            "Zona Deportiva - Garzón",
            
            // Sede La Plata
            "Edificio Principal - La Plata",
            "Aulas - La Plata",
            "Laboratorios - La Plata"
        );
    }

    @Test
    void getAllTipos_ShouldReturnAllTipos() throws Exception {
        // Arrange
        List<String> tipos = getSampleTiposEscenario();
        when(escenarioUseCase.getTiposEscenario()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/tipos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(15)))
                .andExpect(jsonPath("$", hasItem("Aula de Clase")))
                .andExpect(jsonPath("$", hasItem("Laboratorio de Computación")));

        verify(escenarioUseCase, times(1)).getTiposEscenario();
    }

    @Test
    void getAllUbicaciones_ShouldReturnAllUbicaciones() throws Exception {
        // Arrange
        List<String> ubicaciones = getSampleUbicaciones();
        when(escenarioUseCase.getUbicaciones()).thenReturn(ubicaciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/ubicaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(10))))
                .andExpect(jsonPath("$", hasItem(containsString("Edificio de Ingeniería"))))
                .andExpect(jsonPath("$", hasItem(containsString("Pitalito"))));

        verify(escenarioUseCase, times(1)).getUbicaciones();
    }

    @Test
    void getAllTipos_WhenNoTiposExist_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(escenarioUseCase.getTiposEscenario()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/tipos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        verify(escenarioUseCase, times(1)).getTiposEscenario();
    }

    @Test
    void getAllUbicaciones_WhenNoUbicacionesExist_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(escenarioUseCase.getUbicaciones()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/ubicaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        verify(escenarioUseCase, times(1)).getUbicaciones();
    }
}
