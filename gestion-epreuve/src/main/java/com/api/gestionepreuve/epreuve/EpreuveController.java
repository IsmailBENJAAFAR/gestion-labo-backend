package com.api.gestionepreuve.epreuve;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/epreuve")
@RequiredArgsConstructor
public class EpreuveController {
    private final EpreuveService epreuveService;

    @GetMapping()
    public ResponseEntity<List<Epreuve>> getEpreuves() {
        return ResponseEntity.ok(epreuveService.getAllEpreuve());
    }
}
