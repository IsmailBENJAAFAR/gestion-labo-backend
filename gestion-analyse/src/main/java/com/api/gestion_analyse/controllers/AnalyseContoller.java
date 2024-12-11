package com.api.gestion_analyse.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.gestion_analyse.DTO.AnalyseDTO;
import com.api.gestion_analyse.errors.ApiResponse;
import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.services.AnalyseService;

@RestController
@RequestMapping(path = "api/v1/analyses")
@CrossOrigin
public class AnalyseContoller {

    private final AnalyseService analyseService;

    public AnalyseContoller(AnalyseService analyseService) {
        this.analyseService = analyseService;
    }

    @GetMapping
    public List<AnalyseDTO> getAll() {
        return analyseService.getAnalyses();
    }

    @GetMapping(path = "{analyseId}")
    public AnalyseDTO getById(@PathVariable(name = "analyseId") Long analyseId) {
        return analyseService.getAnalyseById(analyseId);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Analyse analyse) {
        return analyseService.createAnalyse(analyse);
    }

    @PutMapping(path = "{analyseId}")
    public ResponseEntity<ApiResponse> update(@PathVariable(name = "analyseId") Long id, @RequestBody Analyse analyse) {
        return analyseService.updateAnalyse(id, analyse);
    }

    @DeleteMapping(path = "{analyseId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable(name = "analyseId") Long id) {
        return analyseService.deleteAnalyse(id);
    }
}
