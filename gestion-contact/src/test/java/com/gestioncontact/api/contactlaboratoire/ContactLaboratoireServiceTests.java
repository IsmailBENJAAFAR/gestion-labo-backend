package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import com.gestioncontact.api.adresse.models.dto.CreateAdresseDTO;
import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.contactlaboratoire.models.dto.ContactLaboratoireDTO;
import com.gestioncontact.api.contactlaboratoire.models.dto.CreateContactLaboratoireDTO;
import com.gestioncontact.api.contactlaboratoire.models.entity.ContactLaboratoire;
import com.gestioncontact.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ContactLaboratoireServiceTests {

    @Mock
    private ContactLaboratoireRepository contactLaboratoireRepository;

    @InjectMocks
    private ContactLaboratoireService contactLaboratoireService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllContactLaboratoires() {
        List<ContactLaboratoire> contacts = List.of(
                new ContactLaboratoire(1, 101, new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"), "0123456789", "0123456789", "contact1@example.com"),
                new ContactLaboratoire(2, 102, new Adresse(2, "2", "Avenue Champs", 75008, "Paris", "Commune2"), "0987654321", "0987654321", "contact2@example.com")
        );

        when(contactLaboratoireRepository.findAll()).thenReturn(contacts);

        List<ContactLaboratoireDTO> result = contactLaboratoireService.getAllContactLaboratoires();

        assertEquals(2, result.size());
        assertEquals("contact1@example.com", result.get(0).getEmail());
        verify(contactLaboratoireRepository, times(1)).findAll();
    }

    @Test
    void shouldFindContactLaboratoireById() {
        ContactLaboratoire contact = new ContactLaboratoire(1, 101, new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"), "0123456789", "0123456789", "contact1@example.com");

        when(contactLaboratoireRepository.findById(1)).thenReturn(Optional.of(contact));

        ContactLaboratoireDTO result = contactLaboratoireService.getContactLaboratoireById(1);

        assertEquals("contact1@example.com", result.getEmail());
        verify(contactLaboratoireRepository, times(1)).findById(1);
    }

    @Test
    void shouldThrowExceptionWhenContactLaboratoireNotFound() {
        when(contactLaboratoireRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contactLaboratoireService.getContactLaboratoireById(99));
        verify(contactLaboratoireRepository, times(1)).findById(99);
    }

    @Test
    void shouldCreateContactLaboratoire() {
        Adresse adresse = new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1");

        ContactLaboratoire contact = new ContactLaboratoire(null, 101, adresse, "0123456789", "0123456789", "contact1@example.com");
        ContactLaboratoire savedContact = new ContactLaboratoire(1, 101, adresse, "0123456789", "0123456789", "contact1@example.com");

        when(contactLaboratoireRepository.save(any(ContactLaboratoire.class))).thenReturn(savedContact);

        CreateContactLaboratoireDTO createDTO = new CreateContactLaboratoireDTO();
        createDTO.setFkIdLaboratoire(101);
        createDTO.setAdresse(new CreateAdresseDTO("1", "Rue de Paris", 75000, "Paris", "Commune1"));
        createDTO.setNumTel("0123456789");
        createDTO.setFax("0123456789");
        createDTO.setEmail("contact1@example.com");

        ContactLaboratoireDTO result = contactLaboratoireService.createContactLaboratoire(createDTO);

        Assertions.assertNotNull(result);
        assertEquals("contact1@example.com", result.getEmail());
        assertEquals(1, result.getId());
        verify(contactLaboratoireRepository, times(1)).save(any(ContactLaboratoire.class));
    }


    @Test
    void shouldUpdateContactLaboratoire() {
        Adresse adresse = new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1");
        ContactLaboratoire existingContact = new ContactLaboratoire(1, 101, adresse, "0123456789", "0123456789", "contact1@example.com");
        ContactLaboratoire updatedContact = new ContactLaboratoire(1, 101, adresse, "0123456789", "0123456789", "updated@example.com");

        when(contactLaboratoireRepository.findById(1)).thenReturn(Optional.of(existingContact));
        when(contactLaboratoireRepository.save(existingContact)).thenReturn(updatedContact);

        ContactLaboratoireDTO updateDTO = new ContactLaboratoireDTO(1, 101, new AdresseDTO(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"), "0123456789", "0123456789", "updated@example.com");

        ContactLaboratoireDTO result = contactLaboratoireService.updateContactLaboratoire(1, updateDTO);

        assertEquals("updated@example.com", result.getEmail());
        verify(contactLaboratoireRepository, times(1)).findById(1);
        verify(contactLaboratoireRepository, times(1)).save(existingContact);
    }

    @Test
    void shouldDeleteContactLaboratoire() {
        ContactLaboratoire contactLaboratoire = new ContactLaboratoire();
        when(contactLaboratoireRepository.findById(1)).thenReturn(Optional.of(contactLaboratoire));

        contactLaboratoireService.deleteContactLaboratoire(1);

        verify(contactLaboratoireRepository, times(1)).delete(contactLaboratoire);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentContactLaboratoire() {
        when(contactLaboratoireRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> contactLaboratoireService.deleteContactLaboratoire(1));
    }
}
