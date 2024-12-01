package com.gestiondossier.api.adresse;

import com.gestiondossier.api.adresse.models.entity.Adresse;
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

@WebMvcTest(AdresseController.class)
@AutoConfigureMockMvc
class AdresseControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AdresseService adresseService;

    List<Adresse> adresses = new ArrayList<>();

    @BeforeEach
    void setUp() {
        adresses = List.of(
                new Adresse(1, "10", "Rue de la Paix", 75001, "Paris", "Paris"),
                new Adresse(2, "22", "Avenue des Champs-Élysées", 75008, "Paris", "Paris")
        );
    }

    @Test
    void shouldFindAllAdresses() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id": 1,
                        "numVoie": "10",
                        "nomVoie": "Rue de la Paix",
                        "codePostal": 75001,
                        "ville": "Paris",
                        "commune": "Paris"
                    },
                    {
                        "id": 2,
                        "numVoie": "22",
                        "nomVoie": "Avenue des Champs-Élysées",
                        "codePostal": 75008,
                        "ville": "Paris",
                        "commune": "Paris"
                    }
                ]
                """;

        when(adresseService.findAll()).thenReturn(adresses);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/adresses"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldFindAdresseWhenGivenValidId() throws Exception {
        Adresse adresse = new Adresse(1, "10", "Rue de la Paix", 75001, "Paris", "Paris");
        when(adresseService.findById(1)).thenReturn(adresse);
        String json = """
                {
                    "id": 1,
                    "numVoie": "10",
                    "nomVoie": "Rue de la Paix",
                    "codePostal": 75001,
                    "ville": "Paris",
                    "commune": "Paris"
                }
                """;

        mockMvc.perform(get("/api/v1/adresses/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldCreateNewAdresse() throws Exception {
        Adresse adresse = new Adresse(1, "10", "Rue de la Paix", 75001, "Paris", "Paris");
        when(adresseService.createAdresse(adresse)).thenReturn(adresse);
        String json = """
                {
                    "id": 1,
                    "numVoie": "10",
                    "nomVoie": "Rue de la Paix",
                    "codePostal": 75001,
                    "ville": "Paris",
                    "commune": "Paris"
                }
                """;

        mockMvc.perform(post("/api/v1/adresses")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldUpdateAdresse() throws Exception {
        Adresse updatedAdresse = new Adresse(1, "12", "Rue de la Paix", 75001, "Paris", "Paris");
        when(adresseService.updateAdresse(1, updatedAdresse)).thenReturn(updatedAdresse);
        String json = """
                {
                    "id": 1,
                    "numVoie": "12",
                    "nomVoie": "Rue de la Paix",
                    "codePostal": 75001,
                    "ville": "Paris",
                    "commune": "Paris"
                }
                """;

        mockMvc.perform(put("/api/v1/adresses/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldDeleteAdresse() throws Exception {
        doNothing().when(adresseService).deleteAdresse(1);

        mockMvc.perform(delete("/api/v1/adresses/1"))
                .andExpect(status().isNoContent());

        verify(adresseService, times(1)).deleteAdresse(1);
    }
}
