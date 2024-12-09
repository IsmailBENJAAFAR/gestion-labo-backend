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

import java.time.LocalDate;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

    ResultActions resultActions = mockMvc.perform(get("/api/v1/laboratoires"))
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

    mockMvc.perform(get("/api/v1/laboratoires/1"))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
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
    String resp = """
        {"message":"Laboratory created successfully"}
        """;

    mockMvc.perform(post("/api/v1/laboratoires")
        .contentType("application/json")
        .content(json))
        .andExpect(status().isCreated())
        .andExpect(content().json(resp));
  }

  

}