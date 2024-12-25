package com.gestioncontact.api.adresse;

import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import com.gestioncontact.api.adresse.models.dto.CreateAdresseDTO;
import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.exception.ResourceNotFoundException;
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

class AdresseServiceTests {

    @Mock
    private AdresseRepository adresseRepository;

    @InjectMocks
    private AdresseService adresseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllAdresses() {
        List<Adresse> adresses = List.of(
                new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1"),
                new Adresse(2, "2", "Avenue des Champs", 75008, "Paris", "Commune2")
        );

        when(adresseRepository.findAll()).thenReturn(adresses);

        List<AdresseDTO> result = adresseService.getAllAdresses();

        assertEquals(2, result.size());
        assertEquals("Rue de Paris", result.get(0).getNomVoie());
        verify(adresseRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAdresseById() {
        Adresse adresse = new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1");

        when(adresseRepository.findById(1)).thenReturn(Optional.of(adresse));

        AdresseDTO result = adresseService.getAdresseById(1);

        assertEquals("Rue de Paris", result.getNomVoie());
        verify(adresseRepository, times(1)).findById(1);
    }

    @Test
    void shouldThrowExceptionWhenAdresseNotFound() {
        when(adresseRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adresseService.getAdresseById(99));
        verify(adresseRepository, times(1)).findById(99);
    }

    @Test
    void shouldCreateAdresse() {
        Adresse adresse = new Adresse(null, "1", "Rue de Paris", 75000, "Paris", "Commune1");
        Adresse savedAdresse = new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1");

        when(adresseRepository.save(adresse)).thenReturn(savedAdresse);

        CreateAdresseDTO createAdresseDTO = new CreateAdresseDTO();
        createAdresseDTO.setNumVoie("1");
        createAdresseDTO.setNomVoie("Rue de Paris");
        createAdresseDTO.setCodePostal(75000);
        createAdresseDTO.setVille("Paris");
        createAdresseDTO.setCommune("Commune1");

        AdresseDTO result = adresseService.createAdresse(createAdresseDTO);

        assertEquals("Rue de Paris", result.getNomVoie());
        verify(adresseRepository, times(1)).save(any(Adresse.class));
    }

    @Test
    void shouldUpdateAdresse() {
        Adresse existingAdresse = new Adresse(1, "1", "Rue de Paris", 75000, "Paris", "Commune1");
        Adresse updatedAdresse = new Adresse(1, "10", "Boulevard Haussmann", 75009, "Paris", "Commune1");

        when(adresseRepository.findById(1)).thenReturn(Optional.of(existingAdresse));
        when(adresseRepository.save(existingAdresse)).thenReturn(updatedAdresse);

        AdresseDTO updateDTO = new AdresseDTO();
        updateDTO.setNumVoie("10");
        updateDTO.setNomVoie("Boulevard Haussmann");
        updateDTO.setCodePostal(75009);
        updateDTO.setVille("Paris");
        updateDTO.setCommune("Commune1");

        AdresseDTO result = adresseService.updateAdresse(1, updateDTO);

        assertEquals("Boulevard Haussmann", result.getNomVoie());
        verify(adresseRepository, times(1)).findById(1);
        verify(adresseRepository, times(1)).save(existingAdresse);
    }

    @Test
    void shouldDeleteAdresse() {
        Adresse adresse = new Adresse();
        when(adresseRepository.findById(1)).thenReturn(Optional.of(adresse));

        adresseService.deleteAdresse(1);

        verify(adresseRepository, times(1)).delete(adresse);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAdresse() {
        when(adresseRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adresseService.deleteAdresse(1));
    }
}
