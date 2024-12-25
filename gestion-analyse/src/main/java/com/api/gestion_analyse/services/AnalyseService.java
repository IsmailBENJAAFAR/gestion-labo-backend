package com.api.gestion_analyse.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gestion_analyse.DTO.AnalyseDTO;
import com.api.gestion_analyse.DTO.AnalyseDTOExtended;
import com.api.gestion_analyse.errors.ApiResponse;
import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.repositores.AnalyseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final AnalyseExternalCommunicationService analyseExternalCommunicationService;
    private final Validator validator;

    public AnalyseService(AnalyseRepository analyseRepository,
            AnalyseExternalCommunicationService analyseExternalCommunicationService) {
        this.analyseExternalCommunicationService = analyseExternalCommunicationService;
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

    public AnalyseDTOExtended getAnalyseById(Long id) {
        Optional<Analyse> analyse = analyseRepository.findById(id);
        if (analyse.isPresent()) {
            Analyse fetchedAnalyse = analyse.get();
            JSONObject laboratoire = analyseExternalCommunicationService
                    .getLaboWithId(fetchedAnalyse.getFkIdLaboratoire());

            if (laboratoire == null)
                throw new UnirestException("Could not communicate with the laboratoire service");
            else if (laboratoire.isEmpty())
                throw new EntityNotFoundException("Analyse not found");

            return new AnalyseDTOExtended(fetchedAnalyse, laboratoire.getString("nom"));

        } else {
            throw new EntityNotFoundException("Analyse not found");
        }
    }

    public ResponseEntity<ApiResponse> createAnalyse(Analyse analyse) {
        if (!validator.validate(analyse).isEmpty()) {
            return new ResponseEntity<>(new ApiResponse("Invalid request"), HttpStatus.BAD_REQUEST);
        }
        JSONObject laboratoireMap = analyseExternalCommunicationService.getLaboWithId(analyse.getFkIdLaboratoire());
        if ((laboratoireMap == null) || (laboratoireMap.isEmpty())) {
            return new ResponseEntity<>(new ApiResponse("Invalid laboratoire id in request"),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            Analyse createdAnalyse = analyseRepository.save(analyse);
            return new ResponseEntity<>(new ApiResponse(createdAnalyse),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("There has been an error when creating this analyse"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateAnalyse(Long id, Analyse analyse) {
        Optional<Analyse> analyseOpt = analyseRepository.findById(id);

        JSONObject laboratoireMap = analyseExternalCommunicationService.getLaboWithId(analyse.getFkIdLaboratoire());
        if ((laboratoireMap == null) || (laboratoireMap.isEmpty())) {
            return new ResponseEntity<>(new ApiResponse("Invalid laboratoire id in request"),
                    HttpStatus.BAD_REQUEST);
        }

        return analyseOpt.map(analyseOld -> {

            analyseOld.setNom(analyse.getNom() != null && !analyse.getNom().isBlank() ? analyse.getNom()
                    : analyseOld.getNom());

            analyseOld.setDescription(analyse.getDescription() != null ? analyse.getDescription()
                    : analyseOld.getDescription());

            // needs check here for foreign key
            analyseOld.setFkIdLaboratoire(analyse.getFkIdLaboratoire() != null ? analyse.getFkIdLaboratoire()
                    : analyseOld.getFkIdLaboratoire());

            return new ResponseEntity<>(new ApiResponse(new AnalyseDTO(analyseOld)), HttpStatus.OK);
        }).orElseGet(() -> {
            return new ResponseEntity<>(new ApiResponse("Analyse not found"), HttpStatus.NOT_FOUND);
        });

    }

    public ResponseEntity<ApiResponse> deleteAnalyse(Long id) {
        Optional<Analyse> analyse = analyseRepository.findById(id);
        if (analyse.isPresent()) {
            // TODO : Needs to add a check into the testAnalyse MS and the epreuve MS
            /* NOTE: this is just a placeholder
             * map<String,String> hasDependencies = analyseExternalCommunicationService.checkDependencies(analyse.getId());
             *   if (!hasDependencies.isEmpty()) {
             *       return new ResponseEntity<>(new ApiResponse("laboratoire had dependencies on" + hasDependencies.keys()),
             *               HttpStatus.BAD_REQUEST);
             * }
             * else{
             *      delete
             * }
             */
            analyseRepository.delete(analyse.get());
            return new ResponseEntity<>(new ApiResponse("Analyse deleted successfully"), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new ApiResponse("Analyse not found"), HttpStatus.NOT_FOUND);
        }
    }
}
