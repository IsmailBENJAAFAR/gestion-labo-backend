package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import com.gestiondossier.api.dossier.models.error.DossierNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/dossiers")
@RequiredArgsConstructor
public class DossierController {
    private final DossierService dossierService;

    @GetMapping
    public List<Dossier> findAll() {
        return dossierService.findAll();
    }

    @GetMapping("/{id}")
    public Dossier findById(@PathVariable("id") Integer dossierId) {
        return Optional.ofNullable(dossierService.findById(dossierId)).orElseThrow(DossierNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dossier createDossier(@RequestBody Dossier dossier) {
        return dossierService.createDossier(dossier);
    }

    @PutMapping("/{id}")
    public Dossier updateDossier(@PathVariable("id") Integer id, @RequestBody Dossier dossier) {
        return Optional.ofNullable(dossierService.updateDossier(id, dossier)).orElseThrow(DossierNotFoundException::new);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteDossier(@PathVariable("id") Integer dossierId) {
        dossierService.deleteDossier(dossierId);
    }

}
