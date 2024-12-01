package com.gestiondossier.api.patient;

import com.gestiondossier.api.patient.models.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc
class PatientControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PatientService patientService;

    List<Patient> patients = new ArrayList<>();

    @BeforeEach
    void setUp() {
        patients = List.of(
                new Patient(1, "benjaafar ismail", null, null, null, null, null, null, null, null),
                new Patient(2, "ayoub ayoub", null, null, null, null, null, null, null, null)
        );
    }

    @Test
    void shouldFindAllPatients() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "nomComplet":"benjaafar ismail",
                        "dateNaissance": null,
                        "sexe": null,
                        "typePieceIdentite": null,
                        "numPieceIdentite": null,
                        "adresse": null,
                        "numTel": null,
                        "email": null,
                        "dossier": null
                    },
                    {
                        "id":2,
                        "nomComplet":"ayoub ayoub",
                        "dateNaissance": null,
                        "sexe": null,
                        "typePieceIdentite": null,
                        "numPieceIdentite": null,
                        "adresse": null,
                        "numTel": null,
                        "email": null,
                        "dossier": null
                    }
                ]
                """;

        when(patientService.findAll()).thenReturn(patients);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldFindPatientWhenGivenValidId() throws Exception {
        Patient patient = new Patient(1, "benjaafar ismail", null, null, null, null, null, null, null, null);
        when(patientService.findById(1)).thenReturn(patient);
        String json = """
                {
                    "id":1,
                    "nomComplet":"benjaafar ismail",
                    "dateNaissance": null,
                    "sexe": null,
                    "typePieceIdentite": null,
                    "numPieceIdentite": null,
                    "adresse": null,
                    "numTel": null,
                    "email": null,
                    "dossier": null
                }
                """;

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldCreateNewPatientWhenGivenValidID() throws Exception {
        Patient patient = new Patient(1, "benjaafar ismail", null, null, null, null, null, null, null, null);
        when(patientService.createPatient(patient)).thenReturn(patient);
        String json = """
                {
                    "id":1,
                    "nomComplet":"benjaafar ismail",
                    "dateNaissance": null,
                    "sexe": null,
                    "typePieceIdentite": null,
                    "numPieceIdentite": null,
                    "adresse": null,
                    "numTel": null,
                    "email": null,
                    "dossier": null
                }
                """;

        mockMvc.perform(post("/api/v1/patients")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldUpdateDossierWhenGivenValidDossier() throws Exception {
        Patient updatedPatient = new Patient(1, "benjaafar ismail", null, null, null, null, null, null, null, null);
        when(patientService.updatePatient(1, updatedPatient)).thenReturn(updatedPatient);
        String json = """
                {
                    "id":1,
                    "nomComplet":"benjaafar ismail",
                    "dateNaissance": null,
                    "sexe": null,
                    "typePieceIdentite": null,
                    "numPieceIdentite": null,
                    "adresse": null,
                    "numTel": null,
                    "email": null,
                    "dossier": null
                }
                """;

        mockMvc.perform(put("/api/v1/patients/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }


    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPatientID() throws Exception {
        String json = """
                {
                    "id":1,
                    "nomComplet":"benjaafar ismail",
                    "dateNaissance": null,
                    "sexe": null,
                    "typePieceIdentite": null,
                    "numPieceIdentite": null,
                    "adresse": null,
                    "numTel": null,
                    "email": null,
                    "dossier": null
                }
                """;

        mockMvc.perform(put("/api/v1/dossiers/999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePatientrWhenGivenValidID() throws Exception {
        doNothing().when(patientService).deletePatient(1);

        mockMvc.perform(delete("/api/v1/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1);
    }

}
