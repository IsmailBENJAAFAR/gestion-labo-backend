package com.api.gestion_laboratoire.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.gestion_laboratoire.dto.LaboratoireDTO;
import com.api.gestion_laboratoire.errors.ApiResponse;
import com.api.gestion_laboratoire.models.Laboratoire;
import com.api.gestion_laboratoire.repositories.LaboratoireRepository;
import com.api.gestion_laboratoire.services.LaboratoireService;

@WebMvcTest(LaboratoireController.class)
@AutoConfigureMockMvc
class LaboratoireControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LaboratoireService laboratoireService;

    Laboratoire laboratoireTest;

    @BeforeEach
    void setup() {
        laboratoireTest = new Laboratoire("labo_x", "123456789", true, null);
        laboratoireTest.setId(1L);
    }

    @Test
    void testGetAll() throws Exception {

        String jsonResp = """
                    [
                      {
                        "id": 1,
                        "nom": "labo_x",
                        "logo": null,
                        "nrc": "123456789",
                        "active": true,
                        "dateActivation": null,
                        "logoID": null
                      }
                    ]
                """;

        List<LaboratoireDTO> laboList = List.of(new LaboratoireDTO(laboratoireTest));

        BDDMockito.when(laboratoireService.getLaboratoires()).thenReturn(laboList);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/laboratoires")).andExpect(status().isOk())
                .andExpect(content().json(jsonResp));

        JSONAssert.assertEquals(jsonResp, resultActions.andReturn().getResponse().getContentAsString(), false);

    }

    @Test
    void testGetById() throws Exception {
        String jsonResp = """
                      {
                        "id": 1,
                        "nom": "labo_x",
                        "logo": null,
                        "nrc": "123456789",
                        "active": true,
                        "dateActivation": null,
                        "logoID": null
                      }
                """;

        LaboratoireDTO laboDTO = new LaboratoireDTO(laboratoireTest);

        BDDMockito.when(laboratoireService.getLaboratoiresById(1L)).thenReturn(laboDTO);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/laboratoires/1")).andExpect(status().isOk())
                .andExpect(content().json(jsonResp));

        JSONAssert.assertEquals(jsonResp, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    // @Test
    // void testCreateLaboratoire() {
    // long laboId = 1L;
    // Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true,
    // LocalDate.now());
    // laboratoire.setId(laboId);
    // ResponseEntity<ApiResponse> response = new ResponseEntity<>(new
    // ApiResponse("creation success"),
    // HttpStatus.CREATED);

    // BDDMockito.when(laboratoireService.createLaboratoire(laboratoire)).thenReturn(response);

    // assertEquals(response.getStatusCode(),
    // laboratoireController.create(laboratoire).getStatusCode());
    // assertEquals(response.getBody().getMessage(),
    // laboratoireController.create(laboratoire).getBody().getMessage());
    // }

    // @Test
    // void testUpdateLaboratoire() {
    // long laboId = 1L;
    // Laboratoire laboratoire = new Laboratoire("labo_y", "123456000", true,
    // LocalDate.now());
    // laboratoire.setId(laboId);
    // ResponseEntity<ApiResponse> response = new ResponseEntity<>(new
    // ApiResponse("update success"),
    // HttpStatus.OK);

    // BDDMockito.when(laboratoireService.updateLaboratoire(laboId,
    // laboratoire)).thenReturn(response);

    // assertEquals(response.getStatusCode(), laboratoireController.update(laboId,
    // laboratoire).getStatusCode());
    // assertEquals(response.getBody().getMessage(),
    // laboratoireController.update(laboId, laboratoire).getBody().getMessage());
    // }

    // @Test
    // void testDeleteLaboratoire() {
    // long laboId = 1L;
    // ResponseEntity<ApiResponse> response = new ResponseEntity<>(new
    // ApiResponse("deletion success"),
    // HttpStatus.OK);

    // BDDMockito.when(laboratoireService.deleteLaboratoire(laboId)).thenReturn(response);

    // assertEquals(response.getStatusCode(),
    // laboratoireController.delete(laboId).getStatusCode());
    // assertEquals(response.getBody().getMessage(),
    // laboratoireController.delete(laboId).getBody().getMessage());
    // }

}
