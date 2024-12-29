package com.gestioncontact.api.adresse;

import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import com.gestioncontact.api.adresse.models.dto.CreateAdresseDTO;
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

@WebMvcTest(AdresseController.class)
@AutoConfigureMockMvc
class AdresseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdresseService adresseService;

    @Test
    void shouldFindAllAdresses() throws Exception {
        List<AdresseDTO> adresses = List.of(
                new AdresseDTO(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"),
                new AdresseDTO(2, "2", "Avenue des Champs", 75008, "Paris", "Commune2")
        );

        when(adresseService.getAllAdresses()).thenReturn(adresses);

        mockMvc.perform(get("/api/v1/adresses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].ville").value("Paris"));
    }

    @Test
    void shouldFindAdresseById() throws Exception {
        AdresseDTO adresse = new AdresseDTO(1, "1", "Rue de Paris", 75000, "Paris", "Commune1");

        when(adresseService.getAdresseById(1)).thenReturn(adresse);

        mockMvc.perform(get("/api/v1/adresses/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ville").value("Paris"));
    }

    @Test
    void shouldCreateAdresse() throws Exception {
        CreateAdresseDTO createAdresseDTO = new CreateAdresseDTO();
        createAdresseDTO.setNumVoie("1");
        createAdresseDTO.setNomVoie("Rue de Paris");
        createAdresseDTO.setCodePostal(75000);
        createAdresseDTO.setVille("Paris");
        createAdresseDTO.setCommune("Commune1");

        AdresseDTO adresseDTO = new AdresseDTO(1, "1", "Rue de Paris", 75000, "Paris", "Commune1");

        when(adresseService.createAdresse(Mockito.any(CreateAdresseDTO.class))).thenReturn(adresseDTO);

        mockMvc.perform(post("/api/v1/adresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "numVoie": "1",
                                    "nomVoie": "Rue de Paris",
                                    "codePostal": 75000,
                                    "ville": "Paris",
                                    "commune": "Commune1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ville").value("Paris"));
    }

    @Test
    void shouldUpdateAdresse() throws Exception {
        AdresseDTO updatedAdresse = new AdresseDTO(1, "10", "Boulevard Haussmann", 75009, "Paris", "Commune1");

        when(adresseService.updateAdresse(Mockito.eq(1), Mockito.any(AdresseDTO.class))).thenReturn(updatedAdresse);

        mockMvc.perform(put("/api/v1/adresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "numVoie": "10",
                                    "nomVoie": "Boulevard Haussmann",
                                    "codePostal": 75009,
                                    "ville": "Paris",
                                    "commune": "Commune1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomVoie").value("Boulevard Haussmann"));
    }

    @Test
    void shouldDeleteAdresse() throws Exception {
        mockMvc.perform(delete("/api/v1/adresses/1"))
                .andExpect(status().isNoContent());
    }
}
