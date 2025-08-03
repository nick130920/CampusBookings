package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.port.input.EscenarioUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TipoEscenarioControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private EscenarioUseCase escenarioUseCase;

    @InjectMocks
    private EscenarioRestController escenarioController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(escenarioController).build();
    }

    @Test
    void getAllTiposEscenario_ShouldReturnListOfTipoEscenario() throws Exception {
        // Arrange
        List<String> tipos = Arrays.asList("Aula", "Auditorio", "Laboratorio");

        when(escenarioUseCase.getTiposEscenario()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/tipos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Aula")))
                .andExpect(jsonPath("$[1]", is("Auditorio")))
                .andExpect(jsonPath("$[2]", is("Laboratorio")));
    }

    @Test
    void getAllUbicaciones_ShouldReturnListOfUbicaciones() throws Exception {
        // Arrange
        List<String> ubicaciones = Arrays.asList("Edificio Central", "Torre Administrativa", "Campus Norte");

        when(escenarioUseCase.getUbicaciones()).thenReturn(ubicaciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/escenarios/ubicaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Edificio Central")))
                .andExpect(jsonPath("$[1]", is("Torre Administrativa")))
                .andExpect(jsonPath("$[2]", is("Campus Norte")));
    }
}
