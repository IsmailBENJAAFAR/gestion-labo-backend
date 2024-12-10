package com.api.analyse.services;

import com.api.analyse.models.Analyse;
import com.api.analyse.repositores.AnalyseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public AnalyseService(AnalyseRepository analyseRepository) {
        this.analyseRepository = analyseRepository;
    }

    public List<Analyse> getAnalyses() {
        return analyseRepository.findAll();
    }

    public Analyse getAnalyseById(Long id) {
        Optional<Analyse> analyse = analyseRepository.findById(id);
        if(analyse.isPresent()) {
            return analyse.get();
        }else{
            throw new EntityNotFoundException("Analyse not found");
        }
    }

    public ResponseEntity<Object> createAnalyse(Analyse analyse) {
//        if(validator.validate(analyse)) {}
        return null;
    }

    public ResponseEntity<Object> updateAnalyse(Long id ,Analyse analyse) {
        return null;
    }

    public ResponseEntity<Object> deleteAnalyse(Long id){
        return null;
    }
}
