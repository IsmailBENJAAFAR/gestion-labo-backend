package com.example.gestion_laboratoire.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gestion_laboratoire.models.Laboratoire;
import com.example.gestion_laboratoire.repositories.LaboratoireRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LaboratoireService {

    private final LaboratoireRepository laboratoireRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public LaboratoireService(LaboratoireRepository laboratoireRepository, CloudinaryService cloudinaryService) {
        this.laboratoireRepository = laboratoireRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Laboratoire> getLaboratoires() {
        return laboratoireRepository.findAll();
    }

    public Laboratoire getLaboratoiresByIdLong(Long id) throws EntityNotFoundException {
        if (laboratoireRepository.existsById(id))
            return laboratoireRepository.findById(id).get();
        else
            throw new EntityNotFoundException("Laboratory Not found");
    }

    public ResponseEntity<Object> createLaboratoire(Laboratoire laboratoire) {
        try {
            String imageName = laboratoire.getNom() + "_" + laboratoire.getId();
            String imageURL = cloudinaryService.uploadImage(imageName, laboratoire.getImageFile());
            if (imageURL == "Failed to upload image") {
                return new ResponseEntity<>("Could not create laboratory : " + imageURL, HttpStatus.FAILED_DEPENDENCY);
            }
            laboratoire.setLogo(imageURL);
            laboratoireRepository.save(laboratoire);
            return new ResponseEntity<>("Laboratory created successfully", HttpStatus.CREATED);
        } catch (Exception ie) {
            return new ResponseEntity<>("Unknown error has occured during the creation of the Laboratory",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<Object> updateLaboratoire(Long id, Laboratoire laboratoire) {
        System.out.println(laboratoire);
        if (laboratoireRepository.existsById(id)) {
            Laboratoire labo = laboratoireRepository.findById(id).get();
            if (laboratoire.getNom() != null && laboratoire.getNom().length() != 0)
                labo.setNom(laboratoire.getNom());
            if (laboratoire.getNrc() != null && laboratoire.getNrc().length() != 0)
                labo.setNrc(laboratoire.getNrc());
            if (laboratoire.getActive() != null)
                labo.setActive(laboratoire.getActive());
            if (laboratoire.getCreatedAt() != null)
                labo.setCreatedAt(laboratoire.getCreatedAt());
            if (laboratoire.getImageFile() != null && laboratoire.getImageFile().length != 0)
                laboratoire.setLogo(cloudinaryService.uploadImage(laboratoire.getNom() + "_" + laboratoire.getId(),
                        laboratoire.getImageFile()));
            return new ResponseEntity<>("Laboratory " + laboratoire.getNom() + " updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Laboratory not found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> deleteLaboratoire(Long id) {
        if (laboratoireRepository.existsById(id)) {
            Laboratoire laboratoire = laboratoireRepository.findById(id).get();
            String imageDeletionMessage = cloudinaryService
                    .deleteImage(laboratoire.getNom() + "_" + laboratoire.getId());
            if (imageDeletionMessage != "Image deleted successfully") {
                return new ResponseEntity<>("Could not delete laboratory, " + imageDeletionMessage,
                        HttpStatus.FAILED_DEPENDENCY);
            }
            laboratoireRepository.deleteById(id);
            return new ResponseEntity<>("Laboratory " + laboratoire.getNom() + " deleted", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Laboratory not found", HttpStatus.NOT_FOUND);
        }
    }

}
