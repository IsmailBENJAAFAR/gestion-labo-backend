package com.api.gestion_laboratoire.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.api.gestion_laboratoire.dto.LaboratoireDTO;
import com.api.gestion_laboratoire.errors.ApiResponse;
import com.api.gestion_laboratoire.models.Laboratoire;
import com.api.gestion_laboratoire.services.LaboratoireService;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LaboratoireController.class)
@AutoConfigureMockMvc
class LaboratoireControllerTests {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  LaboratoireService laboService;

  Laboratoire labo;
  String baseUrl = "/api/v1/laboratoires";

  @BeforeEach
  void setUp() {
    labo = new Laboratoire("x", "123456789", true, LocalDate.of(2011, 11, 11));
    labo.setId(1L);
  }

  @Test
  void shouldFindAllLaboratoires() throws Exception {
    String jsonResp = """
            [
              {
                "id": 1,
                "nom": "x",
                "nrc": "123456789",
                "active": true,
                "dateActivation": "2011-11-11"
              }
            ]
        """;
    when(laboService.getLaboratoires()).thenReturn(List.of(new LaboratoireDTO(labo)));

    ResultActions resultActions = mockMvc.perform(get(baseUrl + ""))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResp));

    JSONAssert.assertEquals(jsonResp, resultActions.andReturn().getResponse().getContentAsString(), false);

  }

  @Test
  void shouldFindLaboratoireWhenGivenValidId() throws Exception {
    when(laboService.getLaboratoiresById(1L)).thenReturn(new LaboratoireDTO(labo));
    String json = """
        {
                "id": 1,
                "nom": "x",
                "nrc": "123456789",
                "active": true,
                "dateActivation": "2011-11-11"
        }
        """;

    mockMvc.perform(get(baseUrl + "/1"))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void shouldReturnNotFoundWhenGivenNonValidId() throws Exception {
    when(laboService.getLaboratoiresById(1L)).thenThrow(new EntityNotFoundException("Laboratory Not found"));

    String errRespJson = """
        {"message": "Laboratory Not found"}
        """;

    mockMvc.perform(get(baseUrl + "/1"))
        .andExpect(status().isNotFound())
        .andExpect(content().json(errRespJson));
  }

  @Test
  void shouldCreateNewLaboratoire() throws Exception {
    when(laboService.createLaboratoire(labo))
        .thenReturn(new ResponseEntity<>(new ApiResponse("Laboratory created successfully"),
            HttpStatus.CREATED));
    String json = """
        {
                "id": 1,
                "nom": "x",
                "nrc": "123456789",
                "active": true,
                "dateActivation": "2011-11-11"
        }
        """;

    mockMvc.perform(post(baseUrl + "")
        .contentType("application/json")
        .content(json))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateLaboratoire() throws Exception {
    Laboratoire updatedLaboratoire = new Laboratoire("rip bozo (I am the bozo)", "123456789", true,
        LocalDate.of(2012, 10, 10));

    when(laboService.updateLaboratoire(1L,
        updatedLaboratoire)).thenReturn(new ResponseEntity<>(new ApiResponse("Laboratory updated"), HttpStatus.OK));

    String json = """
        {
                "id": null,
                "nom": "rip bozo (I am the bozo)",
                "nrc": "123456789",
                "active": true,
                "dateActivation": "2012-10-10"
        }
        """;

    String resp = """
          {"message":"Laboratory updated"}
        """;

    mockMvc.perform(put(baseUrl + "/1")
        .contentType("application/json")
        .content(json))
        .andExpect(status().isOk())
        .andExpect(content().json(resp));
  }

  @Test
  void shouldNotUpdateAndReturnNotFoundHttpRespWhenGivenAnInvalidLaboratoireID() throws Exception {
    Laboratoire updatedLaboratoire = new Laboratoire("rip bozo (I am the bozo)", "123456789", true,
        LocalDate.of(2012, 10, 10));
    String json = """
        {
            "id": null,
            "nom": "rip bozo (I am the bozo)",
            "nrc": "123456789",
            "active": true,
            "dateActivation": "2012-10-10"
        }
        """;

    when(laboService.updateLaboratoire(1L,
        updatedLaboratoire)).thenReturn(new ResponseEntity<>(new ApiResponse("Laboratory not found"), HttpStatus.NOT_FOUND));

    mockMvc.perform(put(baseUrl + "/1")
        .contentType("application/json")
        .content(json))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteLaboratoireWhenGivenValidID() throws Exception {
    when(laboService.deleteLaboratoire(1L)).thenReturn(new ResponseEntity<>(new ApiResponse("Laboratory deleted"),
        HttpStatus.NO_CONTENT));

    mockMvc.perform(delete(baseUrl + "/1"))
        .andExpect(status().isNoContent());

    verify(laboService, times(1)).deleteLaboratoire(1L);
  }
}