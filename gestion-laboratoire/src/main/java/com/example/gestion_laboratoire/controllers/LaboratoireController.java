package com.example.gestion_laboratoire.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gestion_laboratoire.models.Laboratoire;
import com.example.gestion_laboratoire.services.LaboratoireService;

@RestController
// this is kinda like setting the root URL, and then you can set the branch
// urls, cool
@RequestMapping(path = "api/laboratoire")
public class LaboratoireController {

    private final LaboratoireService laboratoireService;

    public LaboratoireController(LaboratoireService laboratoireService) {
        this.laboratoireService = laboratoireService;
    }

    @GetMapping(path = "/")
    public List<Laboratoire> getAll() {
        return laboratoireService.getLaboratoires();
    }

    @GetMapping(path = "{laboId}")
    public Laboratoire getById(@PathVariable(name = "laboId") Long id) throws Exception {
        return laboratoireService.getLaboratoiresByIdLong(id);
    }

    @PostMapping(path = "/")
    public ResponseEntity<?> create(@RequestBody Laboratoire laboratoire) {
        return laboratoireService.createLaboratoire(laboratoire);
    }

    @PutMapping(path = "{laboId}")
    public ResponseEntity<?> update(@PathVariable(name = "laboId") Long id,
            @RequestBody Laboratoire laboratoire) {
        return laboratoireService.updateLaboratoire(id, laboratoire);
    }

    @DeleteMapping(path = "{laboId}")
    public ResponseEntity<?> delete(@PathVariable(name = "laboId") Long id) {
        return laboratoireService.deleteLaboratoire(id);
    }

}
