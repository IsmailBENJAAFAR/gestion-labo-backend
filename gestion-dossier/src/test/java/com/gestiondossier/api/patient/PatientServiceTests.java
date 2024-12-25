package com.gestiondossier.api.patient;

import com.gestiondossier.api.exception.ResourceNotFoundException;
import com.gestiondossier.api.patient.models.dto.CreatePatientDTO;
import com.gestiondossier.api.patient.models.dto.PatientDTO;
import com.gestiondossier.api.patient.models.entity.Patient;
import com.gestiondossier.api.patient.models.entity.Sexe;
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

class PatientServiceTests {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllPatients() {
        List<Patient> patients = List.of(
                new Patient(1, "John Doe", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "john.doe@example.com", null),
                new Patient(2, "Jane Doe", null, Sexe.FEMME, null, null, "456 Elm St", "987654321", "jane.doe@example.com", null)
        );

        when(patientRepository.findAll()).thenReturn(patients);

        List<PatientDTO> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getNomComplet());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void shouldFindPatientById() {
        Patient patient = new Patient(1, "John Doe", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "john.doe@example.com", null);

        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

        PatientDTO result = patientService.getPatientById(1);

        assertEquals("John Doe", result.getNomComplet());
        verify(patientRepository, times(1)).findById(1);
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        when(patientRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(99));
        verify(patientRepository, times(1)).findById(99);
    }

    @Test
    void shouldCreatePatient() {
        Patient patient = new Patient(null, "John Doe", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "john.doe@example.com", null);
        Patient savedPatient = new Patient(1, "John Doe", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "john.doe@example.com", null);

        when(patientRepository.save(patient)).thenReturn(savedPatient);

        PatientDTO result = patientService.createPatient(new CreatePatientDTO("John Doe", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "john.doe@example.com"));

        assertEquals("John Doe", result.getNomComplet());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void shouldUpdatePatient() {
        Patient existingPatient = new Patient(1, "Old Name", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "old@example.com", null);
        Patient updatedPatient = new Patient(1, "Updated Name", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "updated@example.com", null);

        when(patientRepository.findById(1)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(existingPatient)).thenReturn(updatedPatient);

        PatientDTO updateDTO = new PatientDTO(1, "Updated Name", null, null, null, null, "123 Main St", "123456789", "updated@example.com");

        PatientDTO result = patientService.updatePatient(1, updateDTO);

        assertEquals("Updated Name", result.getNomComplet());
        verify(patientRepository, times(1)).findById(1);
        verify(patientRepository, times(1)).save(existingPatient);
    }

    @Test
    void shouldDeletePatient() {
        Patient patient = new Patient(1, "Old Name", null, Sexe.HOMME, null, null, "123 Main St", "123456789", "old@example.com", null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1);

        verify(patientRepository, times(1)).delete(patient);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPatient() {
        when(patientRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> patientService.deletePatient(99));
    }
}
