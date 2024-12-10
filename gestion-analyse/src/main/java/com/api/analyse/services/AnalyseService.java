package com.api.analyse.services;

import com.api.analyse.errors.ApiResponse;
import com.api.analyse.models.Analyse;
import com.api.analyse.repositores.AnalyseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Analyse> getAnalyses() {
        return analyseRepository.findAll();
    }

    public Analyse getAnalyseById(Long id) {
        Optional<Analyse> analyse = analyseRepository.findById(id);
        if (analyse.isPresent()) {
            return analyse.get();
        } else {
            throw new EntityNotFoundException("Analyse not found");
        }
    }

    public ResponseEntity<Object> createAnalyse(Analyse analyse) {
        if (validator.validate(analyse).isEmpty()) {
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
    public ResponseEntity<Object> updateAnalyse(Long id, Analyse analyse) {
        if (validator.validate(analyse).isEmpty()) {
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

    public ResponseEntity<Object> deleteAnalyse(Long id) {
        Optional<Analyse> analyse = analyseRepository.findById(id);
        if (analyse.isPresent()) {
            analyseRepository.delete(analyse.get());
            return new ResponseEntity<>(new ApiResponse("Analyse deleted successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Analyse not found"), HttpStatus.NOT_FOUND);
        }
    }
}
