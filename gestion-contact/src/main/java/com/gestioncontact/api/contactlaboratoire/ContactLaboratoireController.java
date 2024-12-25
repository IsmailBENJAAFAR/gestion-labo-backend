package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.contactlaboratoire.models.dto.ContactLaboratoireDTO;
import com.gestioncontact.api.contactlaboratoire.models.dto.CreateContactLaboratoireDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contactlaboratoires")
@RequiredArgsConstructor
public class ContactLaboratoireController {

    private final ContactLaboratoireService contactLaboratoireService;

    @PostMapping
    public ResponseEntity<ContactLaboratoireDTO> createContactLaboratoire(@RequestBody CreateContactLaboratoireDTO createContactLaboratoireDTO) {
        ContactLaboratoireDTO contactLaboratoireDTO = contactLaboratoireService.createContactLaboratoire(createContactLaboratoireDTO);
        return new ResponseEntity<>(contactLaboratoireDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactLaboratoireDTO> getContactLaboratoireById(@PathVariable Integer id) {
        ContactLaboratoireDTO contactLaboratoireDTO = contactLaboratoireService.getContactLaboratoireById(id);
        return ResponseEntity.ok(contactLaboratoireDTO);
    }

    @GetMapping
    public ResponseEntity<List<ContactLaboratoireDTO>> getAllContactLaboratoires() {
        List<ContactLaboratoireDTO> contactLaboratoires = contactLaboratoireService.getAllContactLaboratoires();
        return ResponseEntity.ok(contactLaboratoires);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactLaboratoireDTO> updateContactLaboratoire(@PathVariable Integer id, @RequestBody ContactLaboratoireDTO contactLaboratoireDTO) {
        ContactLaboratoireDTO updatedContact = contactLaboratoireService.updateContactLaboratoire(id, contactLaboratoireDTO);
        return ResponseEntity.ok(updatedContact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContactLaboratoire(@PathVariable Integer id) {
        contactLaboratoireService.deleteContactLaboratoire(id);
        return ResponseEntity.noContent().build();
    }
}
