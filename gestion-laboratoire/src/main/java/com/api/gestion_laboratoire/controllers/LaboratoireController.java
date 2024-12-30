package com.api.gestion_laboratoire.controllers;

import java.util.List;

import javax.naming.CommunicationException;

import com.api.gestion_laboratoire.errors.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.api.gestion_laboratoire.dto.LaboratoireDTO;
import com.api.gestion_laboratoire.models.Laboratoire;
import com.api.gestion_laboratoire.services.LaboratoireService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping(path = "api/v1/laboratoires")
@CrossOrigin
public class LaboratoireController {

    private final LaboratoireService laboratoireService;

    @Autowired
    public LaboratoireController(LaboratoireService laboratoireService) {
        this.laboratoireService = laboratoireService;
    }

    @GetMapping
    public List<LaboratoireDTO> getAll() {
        return laboratoireService.getLaboratoires();
    }

    @GetMapping(path = "{laboId}")
    public LaboratoireDTO getById(@PathVariable(name = "laboId") Long id)
            throws EntityNotFoundException {
        return laboratoireService.getLaboratoiresById(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Laboratoire laboratoire) {
        return laboratoireService.createLaboratoire(laboratoire);
    }

    @PutMapping(path = "{laboId}")
    public ResponseEntity<ApiResponse> update(@PathVariable(name = "laboId") Long id,
            @RequestBody Laboratoire laboratoire) {
        return laboratoireService.updateLaboratoire(id, laboratoire);
    }

    @DeleteMapping(path = "{laboId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable(name = "laboId") Long id)
            throws CommunicationException, JsonProcessingException {
        return laboratoireService.deleteLaboratoire(id);
    }

}
