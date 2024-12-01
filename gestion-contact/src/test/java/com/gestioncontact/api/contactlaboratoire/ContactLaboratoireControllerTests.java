package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.contactlaboratoire.models.entity.ContactLaboratoire;
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

@WebMvcTest(ContactLaboratoireController.class)
@AutoConfigureMockMvc
class ContactLaboratoireControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ContactLaboratoireService contactLaboratoireService;

    List<ContactLaboratoire> contactLaboratoires = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Adresse adresse1 = Adresse.builder()
                .id(1)
                .numVoie("10")
                .nomVoie("Rue de la Paix")
                .codePostal(75001)
                .ville("Paris")
                .commune("Paris 1er")
                .build();

        Adresse adresse2 = Adresse.builder()
                .id(2)
                .numVoie("20")
                .nomVoie("Avenue Montaigne")
                .codePostal(75008)
                .ville("Paris")
                .commune("Paris 8e")
                .build();

        contactLaboratoires = List.of(
                ContactLaboratoire.builder()
                        .id(1)
                        .fkIdLaboratoire(100)
                        .adresse(adresse1)
                        .numTel("0123456789")
                        .fax("0987654321")
                        .email("labo1@example.com")
                        .build(),
                ContactLaboratoire.builder()
                        .id(2)
                        .fkIdLaboratoire(200)
                        .adresse(adresse2)
                        .numTel("0234567890")
                        .fax("0876543210")
                        .email("labo2@example.com")
                        .build()
        );
    }

    @Test
    void shouldFindAllContactLaboratoires() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id": 1,
                        "fkIdLaboratoire": 100,
                        "adresse": {
                            "id": 1,
                            "numVoie": "10",
                            "nomVoie": "Rue de la Paix",
                            "codePostal": 75001,
                            "ville": "Paris",
                            "commune": "Paris 1er"
                        },
                        "numTel": "0123456789",
                        "fax": "0987654321",
                        "email": "labo1@example.com"
                    },
                    {
                        "id": 2,
                        "fkIdLaboratoire": 200,
                        "adresse": {
                            "id": 2,
                            "numVoie": "20",
                            "nomVoie": "Avenue Montaigne",
                            "codePostal": 75008,
                            "ville": "Paris",
                            "commune": "Paris 8e"
                        },
                        "numTel": "0234567890",
                        "fax": "0876543210",
                        "email": "labo2@example.com"
                    }
                ]
                """;

        when(contactLaboratoireService.findAll()).thenReturn(contactLaboratoires);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/contact-laboratoires"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldFindContactLaboratoireWhenGivenValidId() throws Exception {
        ContactLaboratoire contactLaboratoire = contactLaboratoires.get(0);
        when(contactLaboratoireService.findById(1)).thenReturn(contactLaboratoire);

        String json = """
                {
                    "id": 1,
                    "fkIdLaboratoire": 100,
                    "adresse": {
                        "id": 1,
                        "numVoie": "10",
                        "nomVoie": "Rue de la Paix",
                        "codePostal": 75001,
                        "ville": "Paris",
                        "commune": "Paris 1er"
                    },
                    "numTel": "0123456789",
                    "fax": "0987654321",
                    "email": "labo1@example.com"
                }
                """;

        mockMvc.perform(get("/api/v1/contact-laboratoires/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldCreateNewContactLaboratoire() throws Exception {
        ContactLaboratoire contactLaboratoire = contactLaboratoires.get(0);
        when(contactLaboratoireService.createContactLaboratoire(contactLaboratoire)).thenReturn(contactLaboratoire);

        String json = """
                {
                    "id": 1,
                    "fkIdLaboratoire": 100,
                    "adresse": {
                        "id": 1,
                        "numVoie": "10",
                        "nomVoie": "Rue de la Paix",
                        "codePostal": 75001,
                        "ville": "Paris",
                        "commune": "Paris 1er"
                    },
                    "numTel": "0123456789",
                    "fax": "0987654321",
                    "email": "labo1@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/contact-laboratoires")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldUpdateContactLaboratoireWhenGivenValidContactLaboratoire() throws Exception {
        ContactLaboratoire updatedContactLaboratoire = contactLaboratoires.get(0);
        updatedContactLaboratoire.setEmail("updated@example.com");

        when(contactLaboratoireService.updateContactLaboratoire(1, updatedContactLaboratoire)).thenReturn(updatedContactLaboratoire);

        String json = """
                {
                    "id": 1,
                    "fkIdLaboratoire": 100,
                    "adresse": {
                        "id": 1,
                        "numVoie": "10",
                        "nomVoie": "Rue de la Paix",
                        "codePostal": 75001,
                        "ville": "Paris",
                        "commune": "Paris 1er"
                    },
                    "numTel": "0123456789",
                    "fax": "0987654321",
                    "email": "updated@example.com"
                }
                """;

        mockMvc.perform(put("/api/v1/contact-laboratoires/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidContactLaboratoireID() throws Exception {
        String json = """
                {
                    "id": null,
                    "fkIdLaboratoire": 100,
                    "adresse": {
                        "id": 1,
                        "numVoie": "10",
                        "nomVoie": "Rue de la Paix",
                        "codePostal": 75001,
                        "ville": "Paris",
                        "commune": "Paris 1er"
                    },
                    "numTel": "0123456789",
                    "fax": "0987654321",
                    "email": "labo1@example.com"
                }
                """;

        mockMvc.perform(put("/api/v1/contact-laboratoires/999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteContactLaboratoireWhenGivenValidID() throws Exception {
        doNothing().when(contactLaboratoireService).deleteContactLaboratoire(1);

        mockMvc.perform(delete("/api/v1/contact-laboratoires/1"))
                .andExpect(status().isNoContent());

        verify(contactLaboratoireService, times(1)).deleteContactLaboratoire(1);
    }
}
