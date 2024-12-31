package com.gestiondossier.api.patient;


import com.gestiondossier.api.exception.ResourceNotFoundException;
import com.gestiondossier.api.patient.models.dto.CreatePatientDTO;
import com.gestiondossier.api.patient.models.dto.PatientDTO;
import com.gestiondossier.api.patient.models.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientDTO createPatient(CreatePatientDTO createPatientDTO) {
        Patient patient = Patient.builder()
                .nomComplet(createPatientDTO.getNomComplet())
                .dateNaissance(createPatientDTO.getDateNaissance())
                .sexe(createPatientDTO.getSexe())
                .typePieceIdentite(createPatientDTO.getTypePieceIdentite())
                .numPieceIdentite(createPatientDTO.getNumPieceIdentite())
                .adresse(createPatientDTO.getAdresse())
                .numTel(createPatientDTO.getNumTel())
                .email(createPatientDTO.getEmail())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return mapToDTO(savedPatient);
    }

    public PatientDTO getPatientById(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        return mapToDTO(patient);
    }

    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public PatientDTO updatePatient(Integer id, PatientDTO patientDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        patient.setNomComplet(patientDTO.getNomComplet());
        patient.setDateNaissance(patientDTO.getDateNaissance());
        patient.setSexe(patientDTO.getSexe());
        patient.setTypePieceIdentite(patientDTO.getTypePieceIdentite());
        patient.setNumPieceIdentite(patientDTO.getNumPieceIdentite());
        patient.setAdresse(patientDTO.getAdresse());
        patient.setNumTel(patientDTO.getNumTel());
        patient.setEmail(patientDTO.getEmail());

        Patient updatedPatient = patientRepository.save(patient);
        return mapToDTO(updatedPatient);
    }

    public void deletePatient(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        patientRepository.delete(patient);
    }

    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setNomComplet(patient.getNomComplet());
        dto.setDateNaissance(patient.getDateNaissance());
        dto.setSexe(patient.getSexe());
        dto.setTypePieceIdentite(patient.getTypePieceIdentite());
        dto.setNumPieceIdentite(patient.getNumPieceIdentite());
        dto.setAdresse(patient.getAdresse());
        dto.setNumTel(patient.getNumTel());
        dto.setEmail(patient.getEmail());
        return dto;
    }
}
