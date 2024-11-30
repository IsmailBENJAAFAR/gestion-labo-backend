package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
    void findById() {
    }

    @Test
    void createDossier() {
    }

    @Test
    void updateDossier() {
    }

    @Test
    void deleteDossier() {
    }
}
