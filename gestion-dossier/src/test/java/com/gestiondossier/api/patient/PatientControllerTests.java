package com.gestiondossier.api.patient;

import com.gestiondossier.api.patient.models.dto.CreatePatientDTO;
import com.gestiondossier.api.patient.models.dto.PatientDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc
class PatientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    void shouldFindAllPatients() throws Exception {
        List<PatientDTO> patients = List.of(
                new PatientDTO(1, "John Doe", null, null, null, null, "123 Main St", "123456789", "john.doe@example.com"),
                new PatientDTO(2, "Jane Doe", null, null, null, null, "456 Elm St", "987654321", "jane.doe@example.com")
        );

        when(patientService.getAllPatients()).thenReturn(patients);

        mockMvc.perform(get("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].email").value("jane.doe@example.com"));
    }

    @Test
    void shouldFindPatientById() throws Exception {
        PatientDTO patient = new PatientDTO(1, "John Doe", null, null, null, null, "123 Main St", "123456789", "john.doe@example.com");

        when(patientService.getPatientById(1)).thenReturn(patient);

        mockMvc.perform(get("/api/v1/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void shouldCreatePatient() throws Exception {
        CreatePatientDTO createPatientDTO = new CreatePatientDTO();
        createPatientDTO.setNomComplet("John Doe");
        createPatientDTO.setEmail("john.doe@example.com");

        PatientDTO patientDTO = new PatientDTO(1, "John Doe", null, null, null, null, "123 Main St", "123456789", "john.doe@example.com");

        when(patientService.createPatient(Mockito.any(CreatePatientDTO.class))).thenReturn(patientDTO);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomComplet": "John Doe",
                                    "email": "john.doe@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void shouldUpdatePatient() throws Exception {
        PatientDTO patientDTO = new PatientDTO(1, "Updated Name", null, null, null, null, "123 Main St", "123456789", "updated@example.com");

        when(patientService.updatePatient(Mockito.eq(1), Mockito.any(PatientDTO.class))).thenReturn(patientDTO);

        mockMvc.perform(put("/api/v1/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "nomComplet": "Updated Name",
                                    "email": "updated@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void shouldDeletePatient() throws Exception {
        mockMvc.perform(delete("/api/v1/patients/1"))
                .andExpect(status().isNoContent());
    }
}
