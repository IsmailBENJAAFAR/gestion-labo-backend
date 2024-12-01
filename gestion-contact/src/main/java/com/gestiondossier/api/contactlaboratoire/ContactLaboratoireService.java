package com.gestiondossier.api.contactlaboratoire;

import com.gestiondossier.api.contactlaboratoire.models.entity.ContactLaboratoire;
import com.gestiondossier.api.contactlaboratoire.models.error.ContactLaboratoireNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactLaboratoireService {
    private final ContactLaboratoireRepository contactLaboratoireRepository;

    public List<ContactLaboratoire> findAll() {
        return contactLaboratoireRepository.findAll();
    }

    public ContactLaboratoire findById(Integer id) {
        return contactLaboratoireRepository.findById(id)
                .orElseThrow(ContactLaboratoireNotFoundException::new);
    }

    public ContactLaboratoire createContactLaboratoire(ContactLaboratoire contactLaboratoire) {
        return contactLaboratoireRepository.save(contactLaboratoire);
    }

    @Transactional
    public ContactLaboratoire updateContactLaboratoire(Integer id, ContactLaboratoire requestContactLaboratoire) {
        Optional<ContactLaboratoire> contactLaboratoire = contactLaboratoireRepository.findById(id);
        if (contactLaboratoire.isPresent()) {
            ContactLaboratoire existingContactLaboratoire = findById(id);
            existingContactLaboratoire.setFkIdLaboratoire(requestContactLaboratoire.getFkIdLaboratoire());
            existingContactLaboratoire.setAdresse(requestContactLaboratoire.getAdresse());
            existingContactLaboratoire.setNumTel(requestContactLaboratoire.getNumTel());
            existingContactLaboratoire.setFax(requestContactLaboratoire.getFax());
            existingContactLaboratoire.setEmail(requestContactLaboratoire.getEmail());
            return existingContactLaboratoire;
        } else throw new ContactLaboratoireNotFoundException();
    }

    public void deleteContactLaboratoire(Integer contactLaboratoireId) {
        if (!contactLaboratoireRepository.existsById(contactLaboratoireId)) {
            throw new ContactLaboratoireNotFoundException();
        }
        contactLaboratoireRepository.deleteById(contactLaboratoireId);
    }
}
