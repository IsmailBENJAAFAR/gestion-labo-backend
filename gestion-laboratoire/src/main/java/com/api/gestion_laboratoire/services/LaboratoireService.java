package com.api.gestion_laboratoire.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestion_laboratoire.dto.LaboratoireDTO;
import com.api.gestion_laboratoire.errors.ApiResponse;
import com.api.gestion_laboratoire.models.Laboratoire;
import com.api.gestion_laboratoire.repositories.LaboratoireRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Service
public class LaboratoireService {

    private final LaboratoireRepository laboratoireRepository;
    private final StorageService storageService;
    private final Validator validator;

    public LaboratoireService(LaboratoireRepository laboratoireRepository, StorageService storageService) {
        this.laboratoireRepository = laboratoireRepository;
        this.storageService = storageService;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public List<LaboratoireDTO> getLaboratoires() {
        List<LaboratoireDTO> labos = new ArrayList<>();
        for (Laboratoire laboratoire : laboratoireRepository.findAll()) {
            labos.add(new LaboratoireDTO(laboratoire));
        }
        return labos;
    }

    public LaboratoireDTO getLaboratoiresById(Long id) throws EntityNotFoundException {
        Optional<Laboratoire> optionalLaboratoire = laboratoireRepository.findById(id);
        if (optionalLaboratoire.isPresent())
            return new LaboratoireDTO(optionalLaboratoire.get());
        else
            throw new EntityNotFoundException("Laboratory Not found");
    }

    public ResponseEntity<ApiResponse> createLaboratoire(Laboratoire laboratoire) {

        if (!validator.validate(laboratoire).isEmpty())
            return new ResponseEntity<>(new ApiResponse("Invalid request"),
                    HttpStatus.BAD_REQUEST);

        try {
            Map<String, Object> imageURLInfo = storageService.uploadImage(laboratoire.getImageFile());
            if (imageURLInfo.get("url") == null) {
                return new ResponseEntity<>(
                        new ApiResponse("Could not create laboratory : " + imageURLInfo.get("error")),
                        HttpStatus.FAILED_DEPENDENCY);
            }
            laboratoire.setLogo(String.valueOf(imageURLInfo.get("url")));
            laboratoire.setLogoID(String.valueOf(imageURLInfo.get("display_name")));
            laboratoireRepository.save(laboratoire);
            return new ResponseEntity<>(new ApiResponse("Laboratory created successfully"),
                    HttpStatus.CREATED);
        } catch (Exception ie) {
            return new ResponseEntity<>(
                    new ApiResponse("Unknown error has occured during the creation of the Laboratory"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateLaboratoire(Long id, Laboratoire laboratoire) {
        if (!validator.validate(laboratoire).isEmpty())
            return new ResponseEntity<>(new ApiResponse("Invalid request"),
                    HttpStatus.BAD_REQUEST);

        Optional<Laboratoire> optionalLaboratoire = laboratoireRepository.findById(id);
        if (optionalLaboratoire.isPresent()) {
            Laboratoire labo = optionalLaboratoire.get();
            if (laboratoire.getNom() != null && !laboratoire.getNom().isEmpty())
                labo.setNom(laboratoire.getNom());
            if (laboratoire.getNrc() != null && !laboratoire.getNrc().isEmpty())
                labo.setNrc(laboratoire.getNrc());
            if (laboratoire.getActive() != null)
                labo.setActive(laboratoire.getActive());
            if (laboratoire.getDateActivation() != null)
                labo.setDateActivation(laboratoire.getDateActivation());
            if (laboratoire.getImageFile() != null && laboratoire.getImageFile().length != 0)
                labo.setLogo(storageService.uploadImage(laboratoire.getLogoID(),
                        laboratoire.getImageFile()));

            return new ResponseEntity<>(new ApiResponse("Laboratory updated"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse("Laboratory not found"),
                HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ApiResponse> deleteLaboratoire(Long id) {
        Optional<Laboratoire> optionalLaboratoire = laboratoireRepository.findById(id);
        if (optionalLaboratoire.isPresent()) {
            Laboratoire laboratoire = optionalLaboratoire.get();
            String imageDeletionMessage = storageService
                    .deleteImage(laboratoire.getLogoID());

            if (imageDeletionMessage.contains("Failed to delete image")) {
                return new ResponseEntity<>(
                        new ApiResponse("Could not delete laboratory, " + imageDeletionMessage),
                        HttpStatus.FAILED_DEPENDENCY);
            }
            laboratoireRepository.deleteById(id);
            return new ResponseEntity<>(new ApiResponse("Laboratory deleted"),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Laboratory not found"),
                    HttpStatus.NOT_FOUND);
        }
    }

}
