package com.gestiondossier.api.contactlaboratoire;

import com.gestiondossier.api.contactlaboratoire.models.entity.ContactLaboratoire;
import com.gestiondossier.api.contactlaboratoire.models.error.ContactLaboratoireNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/contact-laboratoires")
@RequiredArgsConstructor
public class ContactLaboratoireController {
    private final ContactLaboratoireService contactLaboratoireService;

    @GetMapping
    public List<ContactLaboratoire> findAll() {
        return contactLaboratoireService.findAll();
    }

    @GetMapping("/{id}")
    public ContactLaboratoire findById(@PathVariable("id") Integer contactLaboratoireId) {
        return Optional.ofNullable(contactLaboratoireService.findById(contactLaboratoireId)).orElseThrow(ContactLaboratoireNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactLaboratoire createContactLaboratoire(@RequestBody ContactLaboratoire contactLaboratoire) {
        return contactLaboratoireService.createContactLaboratoire(contactLaboratoire);
    }

    @PutMapping("/{id}")
    public ContactLaboratoire updateContactLaboratoire(@PathVariable("id") Integer id, @RequestBody ContactLaboratoire contactLaboratoire) {
        return Optional.ofNullable(contactLaboratoireService.updateContactLaboratoire(id, contactLaboratoire)).orElseThrow(ContactLaboratoireNotFoundException::new);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteContactLaboratoire(@PathVariable("id") Integer contactLaboratoireId) {
        contactLaboratoireService.deleteContactLaboratoire(contactLaboratoireId);
    }
}
