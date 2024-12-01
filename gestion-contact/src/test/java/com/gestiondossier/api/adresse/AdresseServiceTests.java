package com.gestiondossier.api.adresse;

import com.gestiondossier.api.adresse.models.entity.Adresse;
import com.gestiondossier.api.adresse.models.error.AdresseNotFoundException;
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

class AdresseServiceTests {
    @Mock
    private AdresseRepository adresseRepository;
    private AutoCloseable autoCloseable;
    private AdresseService adresseService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        adresseService = new AdresseService(adresseRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldReturnAllAdresses() {
        List<Adresse> adresses = List.of(
                new Adresse(1, "10", "Rue de la Paix", 75001, "Paris", "Paris"),
                new Adresse(2, "22", "Avenue des Champs-Élysées", 75008, "Paris", "Paris")
        );

        when(adresseRepository.findAll()).thenReturn(adresses);

        List<Adresse> result = adresseService.findAll();
        verify(adresseRepository).findAll();

        assertEquals(adresses, result);
    }

    @Test
    void shouldFindAdresseById() {
        Adresse adresse = new Adresse(1, "10", "Rue de la Paix", 75001, "Paris", "Paris");

        when(adresseRepository.findById(1)).thenReturn(Optional.of(adresse));

        Adresse result = adresseService.findById(1);
        verify(adresseRepository).findById(1);

        assertEquals(adresse, result);
    }

    @Test
    void shouldThrowAdresseNotFoundExceptionWhileFindingById() {
        when(adresseRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(AdresseNotFoundException.class, () -> {
            adresseService.findById(99);
        });

        verify(adresseRepository).findById(99);
    }

    @Test
    void shouldCreateNewAdresse() {
        Adresse adresse = new Adresse(1, "10", "Rue de la Paix", 75001, "Paris", "Paris");

        when(adresseRepository.save(adresse)).thenReturn(adresse);

        Adresse result = adresseService.createAdresse(adresse);
        verify(adresseRepository).save(adresse);

        assertEquals(adresse, result);
    }

    @Test
    void shouldUpdateExistingAdresse() {
        Adresse oldAdresse = new Adresse(1, "10", "Rue de la Paix", 75001, "Paris", "Paris");
        Adresse updatedAdresse = new Adresse(1, "12", "Rue de la Paix", 75001, "Paris", "Paris");

        when(adresseRepository.findById(1)).thenReturn(Optional.of(oldAdresse));

        Adresse result = adresseService.updateAdresse(1, updatedAdresse);

        assertEquals("12", result.getNumVoie());
    }

    @Test
    void shouldThrowAdresseNotFoundExceptionWhileUpdating() {
        Adresse updatedAdresse = new Adresse(1, "12", "Rue de la Paix", 75001, "Paris", "Paris");

        when(adresseRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(AdresseNotFoundException.class, () -> {
            adresseService.updateAdresse(99, updatedAdresse);
        });

        verify(adresseRepository).findById(99);
    }

    @Test
    void shouldDeleteExistingAdresse() {
        when(adresseRepository.existsById(1)).thenReturn(true);

        adresseService.deleteAdresse(1);
        verify(adresseRepository).deleteById(1);
    }

    @Test
    void shouldThrowAdresseNotFoundExceptionWhileDeleting() {
        when(adresseRepository.existsById(1)).thenReturn(false);

        assertThrows(AdresseNotFoundException.class, () -> {
            adresseService.deleteAdresse(1);
        });
    }
}
