package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.dto.CreateDossierDTO;
import com.gestiondossier.api.dossier.models.dto.DossierDTO;
import com.gestiondossier.api.dossier.models.entity.Dossier;
import com.gestiondossier.api.exception.ResourceNotFoundException;
import com.gestiondossier.api.patient.PatientRepository;
import com.gestiondossier.api.patient.models.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DossierService {

    private final DossierRepository dossierRepository;

    private final PatientRepository patientRepository;

    public DossierDTO createDossier(CreateDossierDTO createDossierDTO) {
        Patient patient = patientRepository.findById(createDossierDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", createDossierDTO.getPatientId()));

        Dossier dossier = Dossier.builder()
                .fkEmailUtilisateur(createDossierDTO.getFkEmailUtilisateur())
                .patient(patient)
                .date(createDossierDTO.getDate())
                .build();

        Dossier savedDossier = dossierRepository.save(dossier);
        return mapToDTO(savedDossier);
    }

    public DossierDTO getDossierById(Integer id) {
        Dossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier", "id", id));
        return mapToDTO(dossier);
    }

    public List<DossierDTO> getAllDossiers() {
        List<Dossier> dossiers = dossierRepository.findAll();
        return dossiers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public DossierDTO updateDossier(Integer id, DossierDTO dossierDTO) {
        Dossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier", "id", id));

        dossier.setFkEmailUtilisateur(dossierDTO.getFkEmailUtilisateur());
        // Suppose que le patient ne peut pas être modifié ici
        dossier.setDate(dossierDTO.getDate());

        Dossier updatedDossier = dossierRepository.save(dossier);
        return mapToDTO(updatedDossier);
    }

    public void deleteDossier(Integer id) {
        Dossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier", "id", id));
        dossierRepository.delete(dossier);
    }

    private DossierDTO mapToDTO(Dossier dossier) {
        DossierDTO dto = new DossierDTO();
        dto.setId(dossier.getId());
        dto.setFkEmailUtilisateur(dossier.getFkEmailUtilisateur());
        dto.setDate(dossier.getDate());

        // Mapper le patient
        if (dossier.getPatient() != null) {
            dto.setPatient(mapPatientToDTO(dossier.getPatient()));
        }

        return dto;
    }

    private com.gestiondossier.api.patient.models.dto.PatientDTO mapPatientToDTO(Patient patient) {
        com.gestiondossier.api.patient.models.dto.PatientDTO dto = new com.gestiondossier.api.patient.models.dto.PatientDTO();
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
