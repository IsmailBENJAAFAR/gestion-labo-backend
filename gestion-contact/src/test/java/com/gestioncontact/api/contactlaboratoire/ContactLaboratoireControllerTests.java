package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import com.gestioncontact.api.adresse.models.dto.CreateAdresseDTO;
import com.gestioncontact.api.contactlaboratoire.models.dto.ContactLaboratoireDTO;
import com.gestioncontact.api.contactlaboratoire.models.dto.CreateContactLaboratoireDTO;
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

@WebMvcTest(ContactLaboratoireController.class)
@AutoConfigureMockMvc
class ContactLaboratoireControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactLaboratoireService contactLaboratoireService;

    @Test
    void shouldFindAllContactLaboratoires() throws Exception {
        List<ContactLaboratoireDTO> contacts = List.of(
                new ContactLaboratoireDTO(1, 101, new AdresseDTO(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"), "0123456789", "0123456789", "contact1@example.com"),
                new ContactLaboratoireDTO(2, 102, new AdresseDTO(2, "2", "Avenue Champs", 75008, "Paris", "Commune2"), "0987654321", "0987654321", "contact2@example.com")
        );

        when(contactLaboratoireService.getAllContactLaboratoires()).thenReturn(contacts);

        mockMvc.perform(get("/api/contactlaboratoires")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].email").value("contact2@example.com"));
    }

    @Test
    void shouldFindContactLaboratoireById() throws Exception {
        ContactLaboratoireDTO contact = new ContactLaboratoireDTO(1, 101, new AdresseDTO(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"), "0123456789", "0123456789", "contact1@example.com");

        when(contactLaboratoireService.getContactLaboratoireById(1)).thenReturn(contact);

        mockMvc.perform(get("/api/contactlaboratoires/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("contact1@example.com"));
    }

    @Test
    void shouldCreateContactLaboratoire() throws Exception {
        CreateContactLaboratoireDTO createDTO = new CreateContactLaboratoireDTO();
        createDTO.setFkIdLaboratoire(101);
        createDTO.setAdresse(new CreateAdresseDTO("1", "Rue de Paris", 75000, "Paris", "Commune1"));
        createDTO.setNumTel("0123456789");
        createDTO.setFax("0123456789");
        createDTO.setEmail("contact1@example.com");

        ContactLaboratoireDTO contactDTO = new ContactLaboratoireDTO(1, 101, new AdresseDTO(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"), "0123456789", "0123456789", "contact1@example.com");

        when(contactLaboratoireService.createContactLaboratoire(Mockito.any(CreateContactLaboratoireDTO.class))).thenReturn(contactDTO);

        mockMvc.perform(post("/api/contactlaboratoires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fkIdLaboratoire": 101,
                                    "adresse": {
                                        "id": 1,
                                        "numVoie": "1",
                                        "nomVoie": "Rue de Paris",
                                        "codePostal": 75000,
                                        "ville": "Paris",
                                        "commune": "Commune1"
                                    },
                                    "numTel": "0123456789",
                                    "fax": "0123456789",
                                    "email": "contact1@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("contact1@example.com"));
    }

    @Test
    void shouldUpdateContactLaboratoire() throws Exception {
        ContactLaboratoireDTO updatedContact = new ContactLaboratoireDTO(1, 101, new AdresseDTO(1, "1", "Boulevard Haussmann", 75009, "Paris", "Commune1"), "0123456789", "0123456789", "updated@example.com");

        when(contactLaboratoireService.updateContactLaboratoire(Mockito.eq(1), Mockito.any(ContactLaboratoireDTO.class))).thenReturn(updatedContact);

        mockMvc.perform(put("/api/contactlaboratoires/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "fkIdLaboratoire": 101,
                                    "adresse": {
                                        "id": 1,
                                        "numVoie": "1",
                                        "nomVoie": "Boulevard Haussmann",
                                        "codePostal": 75009,
                                        "ville": "Paris",
                                        "commune": "Commune1"
                                    },
                                    "numTel": "0123456789",
                                    "fax": "0123456789",
                                    "email": "updated@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void shouldDeleteContactLaboratoire() throws Exception {
        mockMvc.perform(delete("/api/contactlaboratoires/1"))
                .andExpect(status().isNoContent());
    }
}
