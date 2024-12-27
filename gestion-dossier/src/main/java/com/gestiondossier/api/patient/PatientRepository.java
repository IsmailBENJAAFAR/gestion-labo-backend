package com.gestiondossier.api.patient;

import com.gestiondossier.api.patient.models.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
