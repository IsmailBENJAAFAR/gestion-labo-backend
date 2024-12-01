package com.gestiondossier.api.patient;

import com.gestiondossier.api.patient.models.entity.Patient;
import com.gestiondossier.api.patient.models.error.PatientNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Integer id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent())
            return patient.get();
        else
            throw new PatientNotFoundException();

    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatient(Integer id, Patient requestPatient) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            patient.get().setNomComplet(requestPatient.getNomComplet());
            patient.get().setDateNaissance(requestPatient.getDateNaissance());
            patient.get().setSexe(requestPatient.getSexe());
            patient.get().setTypePieceIdentite(requestPatient.getTypePieceIdentite());
            patient.get().setNumPieceIdentite(requestPatient.getNumPieceIdentite());
            patient.get().setAdresse(requestPatient.getAdresse());
            patient.get().setNumTel(requestPatient.getNumTel());
            patient.get().setEmail(requestPatient.getEmail());
            patient.get().setSexe(requestPatient.getSexe());
            return patient.get();
        } else throw new PatientNotFoundException();

    }

    public void deletePatient(Integer patientId) {
        boolean exists = patientRepository.existsById(patientId);
        if (!exists) {
            throw new PatientNotFoundException();
        }
        patientRepository.deleteById(patientId);
    }
}
