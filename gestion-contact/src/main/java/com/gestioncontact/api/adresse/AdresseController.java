package com.gestioncontact.api.adresse;

import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import com.gestioncontact.api.adresse.models.dto.CreateAdresseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/adresses")
@RequiredArgsConstructor
public class AdresseController {

    private final AdresseService adresseService;

    @PostMapping
    public ResponseEntity<AdresseDTO> createAdresse(@RequestBody CreateAdresseDTO createAdresseDTO) {
        AdresseDTO adresseDTO = adresseService.createAdresse(createAdresseDTO);
        return new ResponseEntity<>(adresseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdresseDTO> getAdresseById(@PathVariable Integer id) {
        AdresseDTO adresseDTO = adresseService.getAdresseById(id);
        return ResponseEntity.ok(adresseDTO);
    }

    @GetMapping
    public ResponseEntity<List<AdresseDTO>> getAllAdresses() {
        List<AdresseDTO> adresses = adresseService.getAllAdresses();
        return ResponseEntity.ok(adresses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdresseDTO> updateAdresse(@PathVariable Integer id, @RequestBody AdresseDTO adresseDTO) {
        AdresseDTO updatedAdresse = adresseService.updateAdresse(id, adresseDTO);
        return ResponseEntity.ok(updatedAdresse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdresse(@PathVariable Integer id) {
        adresseService.deleteAdresse(id);
        return ResponseEntity.noContent().build();
    }
}
