package com.api.gestion_analyse.controllers;

import com.api.gestion_analyse.DTO.AnalyseDTO;
import com.api.gestion_analyse.errors.ApiResponse;
import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.services.AnalyseService;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyseContoller.class)
@AutoConfigureMockMvc
class TestAnalyseController {

        @Autowired
        MockMvc mockMvc;

        @MockBean
        AnalyseService analyseService;

        Analyse analyse;
        String baseUrl = "/api/v1/analyses";

        @BeforeEach
        void setUp() {
                analyse = new Analyse("MRI", "MRI something something", 3L);
                analyse.setId(1L);
        }

        @Test
        void shouldFindAllAnalyses() throws Exception {
                String jsonResp = """
                                    [
                                      {
                                        "id": 1,
                                        "nom": "MRI",
                                        "description": "MRI something something",
                                        "fkIdLaboratoire": 3
                                      }
                                    ]
                                """;
                when(analyseService.getAnalyses()).thenReturn(List.of(new AnalyseDTO(analyse)));

                ResultActions resultActions = mockMvc.perform(get(baseUrl))
                                .andExpect(status().isOk())
                                .andExpect(content().json(jsonResp));

                JSONAssert.assertEquals(jsonResp, resultActions.andReturn().getResponse().getContentAsString(), false);

        }

        @Test
        void shouldFindAnalyseWhenGivenValidId() throws Exception {
                when(analyseService.getAnalyseById(1L)).thenReturn(new AnalyseDTO(analyse));
                String json = """
                                {
                                        "id": 1,
                                        "nom": "MRI",
                                        "description": "MRI something something",
                                        "fkIdLaboratoire": 3
                                }
                                """;

                mockMvc.perform(get(baseUrl + "/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().json(json));
        }

        @Test
        void shouldReturnNotFoundWhenGivenNonValidId() throws Exception {
                when(analyseService.getAnalyseById(1L)).thenThrow(new EntityNotFoundException("Analyse Not found"));

                String errRespJson = """
                    {"message": "Analyse Not found"}
                """;

                mockMvc.perform(get(baseUrl + "/1"))
                        .andExpect(status().isNotFound())
                        .andExpect(content().json(errRespJson));
        }

        @Test
        void shouldCreateNewAnalyse() throws Exception {
                String json = """
                                {
                                        "id": 1,
                                        "nom": "MRI",
                                        "description": "MRI something something",
                                        "fkIdLaboratoire": 3
                                }
                                """;
                when(analyseService.createAnalyse(analyse))
                                .thenReturn(new ResponseEntity<>(new ApiResponse("Analyse created successfully"),
                                                HttpStatus.CREATED));

                mockMvc.perform(post(baseUrl)
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isCreated());
        }

        @Test
        void shouldUpdateAnalyse() throws Exception {
                String json = """
                                {
                                        "id": 1,
                                        "nom": "MRI",
                                        "description": "MRI something something",
                                        "fkIdLaboratoire": 3
                                }
                                """;

                String response = """
                                {"message":"Analyse updated successfully"}
                                """;
                when(analyseService.updateAnalyse(1L, analyse))
                                .thenReturn(new ResponseEntity<>(new ApiResponse("Analyse updated successfully"),
                                                HttpStatus.OK));

                mockMvc.perform(put(baseUrl + "/1")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(content().json(response));
        }

        @Test
        void shouldNotUpdateAndReturnNotFoundHttpRespWhenGivenAnInvalidAnalyseID() throws Exception {
                String json = """
                                {
                                        "id": null,
                                        "nom": "MRI",
                                        "description": "MRI something something",
                                        "fkIdLaboratoire": 3
                                }
                                """;
                Analyse updatedAnayse = new Analyse("MRI", "MRI something something", 3L);
                when(analyseService.updateAnalyse(1L, updatedAnayse))
                                .thenReturn(new ResponseEntity<>(new ApiResponse("Analyse not found"),
                                                HttpStatus.NOT_FOUND));

                mockMvc.perform(put(baseUrl + "/1")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldDeleteAnalyseWhenGivenValidID() throws Exception {
                when(analyseService.deleteAnalyse(1L))
                                .thenReturn(new ResponseEntity<>(new ApiResponse("Analyse deleted successfully"),
                                                HttpStatus.NO_CONTENT));

                mockMvc.perform(delete(baseUrl + "/1"))
                                .andExpect(status().isNoContent());

        }
}
