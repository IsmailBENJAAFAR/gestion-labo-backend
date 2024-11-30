package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/dossiers")
@RequiredArgsConstructor
public class DossierController {
    private final DossierService dossierService;

    @GetMapping
    public List<Dossier> findAll() {
        return dossierService.findAll();
    }
}
