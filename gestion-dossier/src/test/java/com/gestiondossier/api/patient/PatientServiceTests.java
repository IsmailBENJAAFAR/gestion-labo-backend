package com.gestiondossier.api.patient;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import com.gestiondossier.api.patient.models.entity.Patient;
import com.gestiondossier.api.patient.models.entity.Sexe;
import com.gestiondossier.api.patient.models.entity.TypePieceIdentite;
import com.gestiondossier.api.patient.models.error.PatientNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatientServiceTests {
    @Mock
    private PatientRepository patientRepository;
    private AutoCloseable autoCloseable;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        patientService = new PatientService(patientRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldReturnAllPatients() {
        List<Patient> patients = List.of(
                new Patient(),
                new Patient(),
                new Patient(),
                new Patient()
        );

        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = patientService.findAll();
        verify(patientRepository).findAll();

        assertEquals(patients, result);
    }

    @Test
    void shouldFindPatientById() {
        List<Patient> patients = List.of(
                new Patient(1, "benjaafar ismail", LocalDate.now(), Sexe.HOMME, TypePieceIdentite.CIN, "F1111", "fes", "212641595440", "benjaafarismail20@gmail.com", new Dossier()),
                new Patient(2, "ayoub ayoub", LocalDate.now(), Sexe.HOMME, TypePieceIdentite.CIN, "F2222", "casa", "212773498565", "ayoub.ayoub@gmail.com", new Dossier()),
                new Patient(3, "rafik rafik", LocalDate.now(), Sexe.HOMME, TypePieceIdentite.CIN, "F3333", "figuig", "212683324576", "rafik.rafik@gmail.com", new Dossier()),
                new Patient(4, "sanaa sanaa", LocalDate.now(), Sexe.FEMME, TypePieceIdentite.CIN, "F4444", "khouribga", "212625171830", "sanaa.sanaa@gmail.com", new Dossier())
        );

        when(patientRepository.findById(1)).thenReturn(Optional.ofNullable(patients.get(0)));

        Patient result = patientService.findById(1);
        verify(patientRepository).findById(1);

        assertEquals(patients.get(0), result);
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhileFindingById() {

        when(patientRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> {
            patientService.findById(99);
        });

        verify(patientRepository).findById(99);
    }

    @Test
    void shouldCreateNewPatient() {
        Patient patient = new Patient(1, "benjaafar ismail", LocalDate.now(), Sexe.HOMME, TypePieceIdentite.CIN, "F1111", "fes", "212641595440", "benjaafarismail20@gmail.com", new Dossier());

        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientService.createPatient(patient);
        verify(patientRepository).save(patient);

        assertEquals(patient, result);
    }

    @Test
    void shouldUpdateExistingPatient() {
        Patient oldPatient = new Patient(1, "benjaafar ismail", LocalDate.now(), Sexe.HOMME, TypePieceIdentite.CIN, "F1111", "fes", "212641595440", "benjaafarismail20@gmail.com", new Dossier());
        Patient updatedPatient = new Patient(1, "benjaafar ismail", LocalDate.now(), Sexe.HOMME, TypePieceIdentite.CIN, "FG6666", "figuig", "212666666666", "benjaafarismail20@gmail.com", new Dossier());

        when(patientRepository.findById(1)).thenReturn(Optional.of(oldPatient));

        Patient result = patientService.updatePatient(1, updatedPatient);

        assertEquals(updatedPatient, result);
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhileUpdating() {
        Patient updatedPatient = new Patient(1, "benjaafar ismail", null, null, null, null, null, null, null, null);
        when(patientRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> {
            patientService.updatePatient(99, updatedPatient);
        });

        verify(patientRepository).findById(99);
    }

    @Test
    void shouldDeleteExistingPatient() {
        when(patientRepository.existsById(1)).thenReturn(true);

        patientService.deletePatient(1);
        verify(patientRepository).deleteById(1);
    }

    @Test
    void shouldThrowPatientNotFoundExceptionWhileDeleting() {
        when(patientRepository.existsById(1)).thenReturn(false);

        assertThrows(PatientNotFoundException.class, () -> {
            patientService.deletePatient(1);
        });

    }
}
