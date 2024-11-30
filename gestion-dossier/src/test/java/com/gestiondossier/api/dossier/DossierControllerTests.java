package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
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

@WebMvcTest(DossierController.class)
@AutoConfigureMockMvc
class DossierControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    DossierService dossierService;

    List<Dossier> dossiers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dossiers = List.of(
                new Dossier(1, "benjaafarismail20@gmail.com", null, null),
                new Dossier(2, "ayoub.ayoub@gmail.com", null, null)
        );
    }

    @Test
    void shouldFindAllDossiers() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "fkEmailUtilisateur":"benjaafarismail20@gmail.com",
                        "patient": null,
                        "date": null
                    },
                    {
                        "id":2,
                        "fkEmailUtilisateur":"ayoub.ayoub@gmail.com",
                        "patient": null,
                        "date": null
                    }
                ]
                """;

        when(dossierService.findAll()).thenReturn(dossiers);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/dossiers"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void shouldFindDossierWhenGivenValidId() throws Exception {
        Dossier dossier = new Dossier(1, "benjaafarismail20@gmail.com", null, null);
        when(dossierService.findById(1)).thenReturn(dossier);
        String json = """
                {
                    "id": 1,
                    "fkEmailUtilisateur": "benjaafarismail20@gmail.com",
                    "patient": null,
                    "date": null
                }
                """;

        mockMvc.perform(get("/api/v1/dossiers/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldCreateNewDossierWhenGivenValidID() throws Exception {
        Dossier dossier = new Dossier(1, "benjaafarismail20@gmail.com", null, null);
        when(dossierService.createDossier(dossier)).thenReturn(dossier);
        String json = """
                {
                    "id": 1,
                    "fkEmailUtilisateur": "benjaafarismail20@gmail.com",
                    "patient": null,
                    "date": null
                }
                """;

        mockMvc.perform(post("/api/v1/dossiers")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldUpdateDossierWhenGivenValidDossier() throws Exception {
        Dossier updatedDossier = new Dossier(1, "benjaafarismail20@gmail.com", null, null);
        when(dossierService.updateDossier(1, updatedDossier)).thenReturn(updatedDossier);
        String json = """
                {
                    "id": 1,
                    "fkEmailUtilisateur": "benjaafarismail20@gmail.com",
                    "patient": null,
                    "date": null
                }
                """;

        mockMvc.perform(put("/api/v1/dossiers/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }


    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidDossierID() throws Exception {
        String json = """
                {
                    "id": null,
                    "fkEmailUtilisateur": "benjaafarismail20@gmail.com",
                    "patient": null,
                    "date": null
                }
                """;

        mockMvc.perform(put("/api/v1/dossiers/999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteDossierWhenGivenValidID() throws Exception {
        doNothing().when(dossierService).deleteDossier(1);

        mockMvc.perform(delete("/api/v1/dossiers/1"))
                .andExpect(status().isNoContent());

        verify(dossierService, times(1)).deleteDossier(1);
    }

}
