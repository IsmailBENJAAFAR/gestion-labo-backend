package com.example.gestion_laboratoire.laboratoire;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Laboratoire getById(@PathVariable(name = "laboId") Long id) {
        return laboratoireService.getLaboratoiresByIdLong(id);
    }

    @PostMapping
    public void create(@RequestBody Laboratoire laboratoire) {
        laboratoireService.createLaboratoire(laboratoire);
    }

    @PutMapping(path = "{laboId}")
    public void update(@PathVariable(name = "laboId") Long id,
            @RequestBody Laboratoire laboratoire) {
        laboratoireService.updateLaboratoire(id, laboratoire);
    }

    @DeleteMapping(path = "{laboId}")
    public void delete(@PathVariable(name = "laboId") Long id) {
        laboratoireService.deleteLaboratoire(id);
    }

}
