package com.api.gestionepreuve.epreuve;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/epreuve")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EpreuveController {
    private final EpreuveService epreuveService;

    @GetMapping()
    public ResponseEntity<List<Epreuve>> getEpreuves() {
        return ResponseEntity.ok(epreuveService.getAllEpreuve());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Epreuve> addEpreuve(@RequestBody Epreuve epreuve) {
        return ResponseEntity.ok(epreuveService.addEpreuve(epreuve));
    }
}
