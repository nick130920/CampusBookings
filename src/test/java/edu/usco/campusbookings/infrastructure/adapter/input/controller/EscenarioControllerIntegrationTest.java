package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.port.input.EscenarioUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(roles = "ADMIN")
class EscenarioControllerIntegrationTest {

    @Mock
    private EscenarioUseCase escenarioUseCase;

    @InjectMocks
    private EscenarioRestController escenarioController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(escenarioController).build();
    }

    @Test
    void getAllTiposEscenario_ShouldReturnListOfTipos() throws Exception {
        // Arrange
        List<String> tipos = Arrays.asList("Cancha de Fútbol", "Cancha de Tenis", "Piscina");
        when(escenarioUseCase.getTiposEscenario()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/tipos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", hasItem("Cancha de Fútbol")))
                .andExpect(jsonPath("$", hasItem("Cancha de Tenis")))
                .andExpect(jsonPath("$", hasItem("Piscina")));
    }

    @Test
    void getAllUbicaciones_ShouldReturnListOfUbicaciones() throws Exception {
        // Arrange
        List<String> ubicaciones = Arrays.asList("Sede Central", "Sede Deportiva", "Polideportivo");
        when(escenarioUseCase.getUbicaciones()).thenReturn(ubicaciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/ubicaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", hasItem("Sede Central")))
                .andExpect(jsonPath("$", hasItem("Sede Deportiva")))
                .andExpect(jsonPath("$", hasItem("Polideportivo")));
    }

    @Test
    void getAllTiposEscenario_WhenNoTiposExist_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(escenarioUseCase.getTiposEscenario()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/tipos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void getAllUbicaciones_WhenNoUbicacionesExist_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(escenarioUseCase.getUbicaciones()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/ubicaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }
}
