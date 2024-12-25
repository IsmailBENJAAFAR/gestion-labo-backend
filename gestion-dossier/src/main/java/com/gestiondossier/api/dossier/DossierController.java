package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.dto.CreateDossierDTO;
import com.gestiondossier.api.dossier.models.dto.DossierDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dossiers")
@RequiredArgsConstructor
public class DossierController {

    private final DossierService dossierService;

    @PostMapping
    public ResponseEntity<DossierDTO> createDossier(@RequestBody CreateDossierDTO createDossierDTO) {
        DossierDTO dossierDTO = dossierService.createDossier(createDossierDTO);
        return new ResponseEntity<>(dossierDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DossierDTO> getDossierById(@PathVariable Integer id) {
        DossierDTO dossierDTO = dossierService.getDossierById(id);
        return ResponseEntity.ok(dossierDTO);
    }

    @GetMapping
    public ResponseEntity<List<DossierDTO>> getAllDossiers() {
        List<DossierDTO> dossiers = dossierService.getAllDossiers();
        return ResponseEntity.ok(dossiers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DossierDTO> updateDossier(@PathVariable Integer id, @RequestBody DossierDTO dossierDTO) {
        DossierDTO updatedDossier = dossierService.updateDossier(id, dossierDTO);
        return ResponseEntity.ok(updatedDossier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDossier(@PathVariable Integer id) {
        dossierService.deleteDossier(id);
        return ResponseEntity.noContent().build();
    }
}
