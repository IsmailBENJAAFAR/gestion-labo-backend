package com.api.gestion_analyse.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestion_analyse.DTO.AnalyseDTO;
import com.api.gestion_analyse.errors.ApiResponse;
import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.repositores.AnalyseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final Validator validator;

    public AnalyseService(AnalyseRepository analyseRepository) {
        this.analyseRepository = analyseRepository;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public List<AnalyseDTO> getAnalyses() {
        List<AnalyseDTO> listAnalyseDTO = new ArrayList<>();

        for (Analyse analyse : analyseRepository.findAll()) {
            listAnalyseDTO.add(new AnalyseDTO(analyse));
        }
        return listAnalyseDTO;
    }

    public AnalyseDTO getAnalyseById(Long id) {
        Optional<Analyse> analyse = analyseRepository.findById(id);
        if (analyse.isPresent()) {
            return new AnalyseDTO(analyse.get());
        } else {
            throw new EntityNotFoundException("Analyse not found");
        }
    }

    public ResponseEntity<ApiResponse> createAnalyse(Analyse analyse) {
        System.out.println(analyse);
        if (!validator.validate(analyse).isEmpty()) {
            return new ResponseEntity<>(new ApiResponse("Invalid request"), HttpStatus.BAD_REQUEST);
        }
        try {
            analyseRepository.save(analyse);
            return new ResponseEntity<>(new ApiResponse("Analyse created successfully"),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("There has been an error when creating this analyse"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateAnalyse(Long id, Analyse analyse) {
        if (!validator.validate(analyse).isEmpty()) {
            return new ResponseEntity<>(new ApiResponse("Invalid request"), HttpStatus.BAD_REQUEST);
        }
        Optional<Analyse> analyseOpt = analyseRepository.findById(id);
        if (analyseOpt.isPresent()) {
            Analyse analyseOld = analyseOpt.get();
            if (analyse.getNom() != null && !analyse.getNom().isEmpty()) {
                analyseOld.setNom(analyse.getNom());
            }
            if (analyse.getDescription() != null) {
                analyseOld.setDescription(analyse.getDescription());
            }
            if (analyse.getFkIdLaboratoire() != null) {
                // needs check here for foreign key
                analyseOld.setFkIdLaboratoire(analyse.getFkIdLaboratoire());
            }
            return new ResponseEntity<>(new ApiResponse("Analyse updated successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Analyse not found"), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse> deleteAnalyse(Long id) {
        Optional<Analyse> analyse = analyseRepository.findById(id);
        if (analyse.isPresent()) {
            analyseRepository.delete(analyse.get());
            return new ResponseEntity<>(new ApiResponse("Analyse deleted successfully"), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new ApiResponse("Analyse not found"), HttpStatus.NOT_FOUND);
        }
    }
}
