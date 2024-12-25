package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.dto.CreateDossierDTO;
import com.gestiondossier.api.dossier.models.dto.DossierDTO;
import com.gestiondossier.api.dossier.models.entity.Dossier;
import com.gestiondossier.api.exception.ResourceNotFoundException;
import com.gestiondossier.api.patient.PatientRepository;
import com.gestiondossier.api.patient.models.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DossierServiceTests {

    @Mock
    private DossierRepository dossierRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private DossierService dossierService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllDossiers() {
        List<Dossier> dossiers = List.of(
                new Dossier(1, "benjaafarismail20@gmail.com", new Patient(), LocalDate.now()),
                new Dossier(2, "ayoub.ayoub@gmail.com", new Patient(), LocalDate.now())
        );

        when(dossierRepository.findAll()).thenReturn(dossiers);

        List<DossierDTO> result = dossierService.getAllDossiers();

        assertEquals(2, result.size());
        assertEquals("benjaafarismail20@gmail.com", result.get(0).getFkEmailUtilisateur());
        verify(dossierRepository, times(1)).findAll();
    }

    @Test
    void shouldFindDossierById() {
        Dossier dossier = new Dossier(1, "benjaafarismail20@gmail.com", new Patient(), LocalDate.now());

        when(dossierRepository.findById(1)).thenReturn(Optional.of(dossier));

        DossierDTO result = dossierService.getDossierById(1);

        assertEquals("benjaafarismail20@gmail.com", result.getFkEmailUtilisateur());
        verify(dossierRepository, times(1)).findById(1);
    }

    @Test
    void shouldThrowExceptionWhenDossierNotFound() {
        when(dossierRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dossierService.getDossierById(99));
        verify(dossierRepository, times(1)).findById(99);
    }

    @Test
    void shouldCreateDossier() {
        Patient patient = new Patient();
        patient.setId(1);

        Dossier dossier = new Dossier(null, "new.email@example.com", patient, LocalDate.now());
        Dossier savedDossier = new Dossier(1, "new.email@example.com", patient, LocalDate.now());

        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(dossierRepository.save(dossier)).thenReturn(savedDossier);

        DossierDTO result = dossierService.createDossier(new CreateDossierDTO("new.email@example.com", 1, LocalDate.now()));

        assertEquals("new.email@example.com", result.getFkEmailUtilisateur());
        verify(dossierRepository, times(1)).save(dossier);
    }

    @Test
    void shouldUpdateDossier() {
        Dossier existingDossier = new Dossier(1, "old.email@example.com", null, LocalDate.now());
        Dossier updatedDossier = new Dossier(1, "updated.email@example.com", null, LocalDate.now());

        when(dossierRepository.findById(1)).thenReturn(Optional.of(existingDossier));
        when(dossierRepository.save(existingDossier)).thenReturn(updatedDossier);

        DossierDTO result = dossierService.updateDossier(1, new DossierDTO(1, "updated.email@example.com", null, LocalDate.now()));

        assertEquals("updated.email@example.com", result.getFkEmailUtilisateur());
        verify(dossierRepository, times(1)).findById(1);
        verify(dossierRepository, times(1)).save(existingDossier);
    }

    @Test
    void shouldDeleteDossier() {
        Dossier dossier = new Dossier(1, "old.email@example.com", null, LocalDate.now());
        when(dossierRepository.findById(1)).thenReturn(Optional.of(dossier));

        dossierService.deleteDossier(1);

        verify(dossierRepository, times(1)).delete(dossier);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentDossier() {
        when(dossierRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> dossierService.deleteDossier(1));
    }
}
