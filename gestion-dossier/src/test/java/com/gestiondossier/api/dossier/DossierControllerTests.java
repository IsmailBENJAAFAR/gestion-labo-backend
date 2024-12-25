package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.dto.CreateDossierDTO;
import com.gestiondossier.api.dossier.models.dto.DossierDTO;
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

@WebMvcTest(DossierController.class)
@AutoConfigureMockMvc
class DossierControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DossierService dossierService;

    @Test
    void shouldFindAllDossiers() throws Exception {
        List<DossierDTO> dossiers = List.of(
                new DossierDTO(1, "benjaafarismail20@gmail.com", null, null),
                new DossierDTO(2, "ayoub.ayoub@gmail.com", null, null)
        );

        when(dossierService.getAllDossiers()).thenReturn(dossiers);

        mockMvc.perform(get("/api/dossiers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].fkEmailUtilisateur").value("ayoub.ayoub@gmail.com"));
    }

    @Test
    void shouldFindDossierById() throws Exception {
        DossierDTO dossier = new DossierDTO(1, "benjaafarismail20@gmail.com", null, null);

        when(dossierService.getDossierById(1)).thenReturn(dossier);

        mockMvc.perform(get("/api/dossiers/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fkEmailUtilisateur").value("benjaafarismail20@gmail.com"));
    }

    @Test
    void shouldCreateDossier() throws Exception {
        CreateDossierDTO createDossierDTO = new CreateDossierDTO();
        createDossierDTO.setFkEmailUtilisateur("benjaafarismail20@gmail.com");
        createDossierDTO.setPatientId(1);

        DossierDTO dossierDTO = new DossierDTO(1, "benjaafarismail20@gmail.com", null, null);

        when(dossierService.createDossier(Mockito.any(CreateDossierDTO.class))).thenReturn(dossierDTO);

        mockMvc.perform(post("/api/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fkEmailUtilisateur": "benjaafarismail20@gmail.com",
                                    "patientId": 1,
                                    "date": null
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fkEmailUtilisateur").value("benjaafarismail20@gmail.com"));
    }

    @Test
    void shouldUpdateDossier() throws Exception {
        DossierDTO dossierDTO = new DossierDTO(1, "updated.email@example.com", null, null);

        when(dossierService.updateDossier(Mockito.eq(1), Mockito.any(DossierDTO.class))).thenReturn(dossierDTO);

        mockMvc.perform(put("/api/dossiers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "fkEmailUtilisateur": "updated.email@example.com",
                                    "patient": null,
                                    "date": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fkEmailUtilisateur").value("updated.email@example.com"));
    }

    @Test
    void shouldDeleteDossier() throws Exception {
        mockMvc.perform(delete("/api/dossiers/1"))
                .andExpect(status().isNoContent());
    }
}
