package com.example.gestion_laboratoire.laboratoire;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    }

    @GetMapping(path = "/")
    public Laboratoire getById() {

    }

    @PostMapping
    public void create() {

    }

    @PutMapping
    public void update() {

    }

    @DeleteMapping
    public void delete(){
        
    }

}
