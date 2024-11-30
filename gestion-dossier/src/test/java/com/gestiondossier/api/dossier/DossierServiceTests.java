package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import com.gestiondossier.api.patient.models.entity.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DossierServiceTests {
    @Mock
    private DossierRepository dossierRepository;
    private AutoCloseable autoCloseable;
    private DossierService dossierService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        dossierService = new DossierService(dossierRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldReturnAllDossiers() {
        List<Dossier> dossiers = List.of(
                new Dossier(),
                new Dossier(),
                new Dossier(),
                new Dossier()
        );

        when(dossierRepository.findAll()).thenReturn(dossiers);

        List<Dossier> result = dossierService.findAll();
        verify(dossierRepository).findAll();

        assertEquals(dossiers, result);
    }

    @Test
    void shouldFindDossierById() {
        List<Dossier> dossiers = List.of(
                new Dossier(1, "benjaafarismail20@gmail.com", new Patient(), LocalDate.now()),
                new Dossier(2, "ayoub.ayoub@gmail.com", new Patient(), LocalDate.now()),
                new Dossier(2, "imade.imade@gmail.com", new Patient(), LocalDate.now()),
                new Dossier(2, "omar.omar@gmail.com", new Patient(), LocalDate.now())
        );

        when(dossierRepository.findById(1)).thenReturn(Optional.ofNullable(dossiers.get(0)));

        Dossier result = dossierService.findById(1);
        verify(dossierRepository).findById(1);

        assertEquals(dossiers.get(0), result);
    }

    @Test
    void shouldCreateNewDossier() {
        Dossier dossier = new Dossier(1, "benjaafarismail20@gmail.com", new Patient(), LocalDate.now());

        when(dossierRepository.save(dossier)).thenReturn(dossier);

        Dossier result = dossierService.createDossier(dossier);
        verify(dossierRepository).save(dossier);

        assertEquals(dossier, result);
    }

    @Test
    void shouldUpdateExistingDossier() {
        Dossier oldDossier = new Dossier(1, "benjaafarismail20@gmail.com", new Patient(), LocalDate.now());
        Dossier updatedDossier = new Dossier(1, "ismail.ismail@gmail.com", new Patient(), LocalDate.now());

        when(dossierRepository.findById(1)).thenReturn(Optional.of(oldDossier));

        Dossier result = dossierService.updateDossier(1, updatedDossier);

        assertEquals(updatedDossier, result);
    }
}
