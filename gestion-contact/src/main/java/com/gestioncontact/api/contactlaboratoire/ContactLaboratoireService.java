package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.adresse.AdresseService;
import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.contactlaboratoire.models.dto.ContactLaboratoireDTO;
import com.gestioncontact.api.contactlaboratoire.models.dto.CreateContactLaboratoireDTO;
import com.gestioncontact.api.contactlaboratoire.models.entity.ContactLaboratoire;
import com.gestioncontact.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactLaboratoireService {

    private final ContactLaboratoireRepository contactLaboratoireRepository;

    private final AdresseService adresseService;

    public ContactLaboratoireDTO createContactLaboratoire(CreateContactLaboratoireDTO createContactLaboratoireDTO) {
        // Créez l'adresse à partir du DTO
        Adresse adresse = Adresse.builder()
                .numVoie(createContactLaboratoireDTO.getAdresse().getNumVoie())
                .nomVoie(createContactLaboratoireDTO.getAdresse().getNomVoie())
                .codePostal(createContactLaboratoireDTO.getAdresse().getCodePostal())
                .ville(createContactLaboratoireDTO.getAdresse().getVille())
                .commune(createContactLaboratoireDTO.getAdresse().getCommune())
                .build();

        // Créez l'entité ContactLaboratoire avec l'adresse
        ContactLaboratoire contactLaboratoire = ContactLaboratoire.builder()
                .fkIdLaboratoire(createContactLaboratoireDTO.getFkIdLaboratoire())
                .adresse(adresse) // Utilisation directe de l'entité Adresse
                .numTel(createContactLaboratoireDTO.getNumTel())
                .fax(createContactLaboratoireDTO.getFax())
                .email(createContactLaboratoireDTO.getEmail())
                .build();

        // Sauvegardez l'entité ContactLaboratoire
        ContactLaboratoire savedContact = contactLaboratoireRepository.save(contactLaboratoire);

        // Mappez l'entité sauvegardée en DTO pour la réponse
        return mapToDTO(savedContact);
    }


    public ContactLaboratoireDTO getContactLaboratoireById(Integer id) {
        ContactLaboratoire contact = contactLaboratoireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactLaboratoire", "id", id));
        return mapToDTO(contact);
    }

    public List<ContactLaboratoireDTO> getAllContactLaboratoires() {
        List<ContactLaboratoire> contacts = contactLaboratoireRepository.findAll();
        return contacts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ContactLaboratoireDTO updateContactLaboratoire(Integer id, ContactLaboratoireDTO contactLaboratoireDTO) {
        ContactLaboratoire contact = contactLaboratoireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactLaboratoire", "id", id));

        contact.setFkIdLaboratoire(contactLaboratoireDTO.getFkIdLaboratoire());
        contact.setNumTel(contactLaboratoireDTO.getNumTel());
        contact.setFax(contactLaboratoireDTO.getFax());
        contact.setEmail(contactLaboratoireDTO.getEmail());

        ContactLaboratoire updatedContact = contactLaboratoireRepository.save(contact);
        return mapToDTO(updatedContact);
    }

    public void deleteContactLaboratoire(Integer id) {
        ContactLaboratoire contact = contactLaboratoireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactLaboratoire", "id", id));
        contactLaboratoireRepository.delete(contact);
    }

    private ContactLaboratoireDTO mapToDTO(ContactLaboratoire contact) {
        if (contact == null) {
            return null;
        }

        ContactLaboratoireDTO dto = new ContactLaboratoireDTO();
        dto.setId(contact.getId());
        dto.setFkIdLaboratoire(contact.getFkIdLaboratoire());

        if (contact.getAdresse() != null) {
            dto.setAdresse(mapAdresseToDTO(contact.getAdresse()));
        }

        dto.setNumTel(contact.getNumTel());
        dto.setFax(contact.getFax());
        dto.setEmail(contact.getEmail());

        return dto;
    }

    private AdresseDTO mapAdresseToDTO(Adresse adresse) {
        if (adresse == null) {
            return null;
        }

        AdresseDTO dto = new AdresseDTO();
        dto.setId(adresse.getId());
        dto.setNumVoie(adresse.getNumVoie());
        dto.setNomVoie(adresse.getNomVoie());
        dto.setCodePostal(adresse.getCodePostal());
        dto.setVille(adresse.getVille());
        dto.setCommune(adresse.getCommune());

        return dto;
    }


}
