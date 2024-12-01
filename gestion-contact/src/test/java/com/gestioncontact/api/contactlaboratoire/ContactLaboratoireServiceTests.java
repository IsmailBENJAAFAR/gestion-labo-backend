package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.contactlaboratoire.models.entity.ContactLaboratoire;
import com.gestioncontact.api.contactlaboratoire.models.error.ContactLaboratoireNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContactLaboratoireServiceTests {
    @Mock
    private ContactLaboratoireRepository contactLaboratoireRepository;
    private AutoCloseable autoCloseable;
    private ContactLaboratoireService contactLaboratoireService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        contactLaboratoireService = new ContactLaboratoireService(contactLaboratoireRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldReturnAllContactLaboratoires() {
        Adresse adresse1 = Adresse.builder()
                .numVoie("10")
                .nomVoie("Rue de la Paix")
                .build();

        Adresse adresse2 = Adresse.builder()
                .numVoie("20")
                .nomVoie("Avenue des Champs")
                .build();

        List<ContactLaboratoire> contacts = List.of(
                new ContactLaboratoire(1, 1, adresse1, "0123456789", "0987654321", "contact1@lab.com"),
                new ContactLaboratoire(2, 2, adresse2, "0234567890", "0876543210", "contact2@lab.com")
        );

        when(contactLaboratoireRepository.findAll()).thenReturn(contacts);

        List<ContactLaboratoire> result = contactLaboratoireService.findAll();
        verify(contactLaboratoireRepository).findAll();

        assertEquals(contacts, result);
    }

    @Test
    void shouldFindContactLaboratoireById() {
        Adresse adresse = Adresse.builder()
                .numVoie("10")
                .nomVoie("Rue de la Paix")
                .build();

        ContactLaboratoire contact = new ContactLaboratoire(1, 1, adresse, "0123456789", "0987654321", "contact1@lab.com");

        when(contactLaboratoireRepository.findById(1)).thenReturn(Optional.of(contact));

        ContactLaboratoire result = contactLaboratoireService.findById(1);
        verify(contactLaboratoireRepository).findById(1);

        assertEquals(contact, result);
    }

    @Test
    void shouldThrowContactLaboratoireNotFoundExceptionWhileFindingById() {
        when(contactLaboratoireRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ContactLaboratoireNotFoundException.class, () -> {
            contactLaboratoireService.findById(99);
        });

        verify(contactLaboratoireRepository).findById(99);
    }

    @Test
    void shouldCreateNewContactLaboratoire() {
        Adresse adresse = Adresse.builder()
                .numVoie("10")
                .nomVoie("Rue de la Paix")
                .build();

        ContactLaboratoire contact = new ContactLaboratoire(1, 1, adresse, "0123456789", "0987654321", "contact1@lab.com");

        when(contactLaboratoireRepository.save(contact)).thenReturn(contact);

        ContactLaboratoire result = contactLaboratoireService.createContactLaboratoire(contact);
        verify(contactLaboratoireRepository).save(contact);

        assertEquals(contact, result);
    }

    @Test
    void shouldUpdateExistingContactLaboratoire() {
        Adresse adresse = Adresse.builder()
                .numVoie("10")
                .nomVoie("Rue de la Paix")
                .build();

        ContactLaboratoire oldContact = new ContactLaboratoire(1, 1, adresse, "0123456789", "0987654321", "contact1@lab.com");
        ContactLaboratoire updatedContact = new ContactLaboratoire(1, 1, adresse, "0987654321", "0123456789", "updated@lab.com");

        when(contactLaboratoireRepository.findById(1)).thenReturn(Optional.of(oldContact));

        ContactLaboratoire result = contactLaboratoireService.updateContactLaboratoire(1, updatedContact);

        assertEquals(updatedContact, result);
    }

    @Test
    void shouldThrowContactLaboratoireNotFoundExceptionWhileUpdating() {
        Adresse adresse = Adresse.builder()
                .numVoie("10")
                .nomVoie("Rue de la Paix")
                .build();

        ContactLaboratoire updatedContact = new ContactLaboratoire(1, 1, adresse, "0987654321", "0123456789", "updated@lab.com");

        when(contactLaboratoireRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ContactLaboratoireNotFoundException.class, () -> {
            contactLaboratoireService.updateContactLaboratoire(99, updatedContact);
        });

        verify(contactLaboratoireRepository).findById(99);
    }

    @Test
    void shouldDeleteExistingContactLaboratoire() {
        when(contactLaboratoireRepository.existsById(1)).thenReturn(true);

        contactLaboratoireService.deleteContactLaboratoire(1);
        verify(contactLaboratoireRepository).deleteById(1);
    }

    @Test
    void shouldThrowContactLaboratoireNotFoundExceptionWhileDeleting() {
        when(contactLaboratoireRepository.existsById(1)).thenReturn(false);

        assertThrows(ContactLaboratoireNotFoundException.class, () -> {
            contactLaboratoireService.deleteContactLaboratoire(1);
        });
    }
}
