package com.gestioncontact.api.adresse;

import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.adresse.models.error.AdresseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/adresses")
@RequiredArgsConstructor
public class AdresseController {
    private final AdresseService adresseService;

    @GetMapping
    public List<Adresse> findAll() {
        return adresseService.findAll();
    }

    @GetMapping("/{id}")
    public Adresse findById(@PathVariable("id") Integer adresseId) {
        return Optional.ofNullable(adresseService.findById(adresseId)).orElseThrow(AdresseNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Adresse createAdresse(@RequestBody Adresse adresse) {
        return adresseService.createAdresse(adresse);
    }

    @PutMapping("/{id}")
    public Adresse updateAdresse(@PathVariable("id") Integer id, @RequestBody Adresse adresse) {
        return Optional.ofNullable(adresseService.updateAdresse(id, adresse)).orElseThrow(AdresseNotFoundException::new);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteAdresse(@PathVariable("id") Integer adresseId) {
        adresseService.deleteAdresse(adresseId);
    }
}
