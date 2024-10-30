package com.example.gestion_laboratoire.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gestion_laboratoire.Models.Laboratoire;
import com.example.gestion_laboratoire.repositories.LaboratoireRepository;

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

    public Laboratoire getLaboratoiresByIdLong(Long id) throws Exception {
        if (laboratoireRepository.existsById(id))
            return laboratoireRepository.findById(id).get();
        else
            throw new Exception("Laboratoire Not found");
    }

    public String createLaboratoire(Laboratoire laboratoire) {
        try {
            System.out.println(laboratoire);
            String imageName = laboratoire.getNom() + "_" + laboratoire.getId();
            String imageURL = cloudinaryService.uploadImage(imageName, laboratoire.getImageFile());
            if (imageURL == "Failed to upload image") {
                return imageURL;
            }
            laboratoire.setLogo(imageURL);
            laboratoireRepository.save(laboratoire);
            return "Laboratoire created successfully";
        } catch (IllegalArgumentException ie) {
            return "Unknown error has occured during the creation of the Laboratoire";
        }
    }

    @Transactional
    public String updateLaboratoire(Long id, Laboratoire laboratoire) {
        System.out.println(laboratoire);
        try {
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
            return "Laboratoire " + laboratoire.getNom() + " updated";

        } catch (Exception e) {
            return "laboratoire Not Found";
        }
    }

    public String deleteLaboratoire(Long id) {
        try {
            Laboratoire laboratoire = laboratoireRepository.findById(id).get();
            cloudinaryService.deleteImage(laboratoire.getNom() + "_" + laboratoire.getId());
            laboratoireRepository.deleteById(id);
            return "Laboratoire " + laboratoire.getNom() + " deleted";
        } catch (NoSuchElementException e) {
            return "laboratoire Not Found";
        }
    }

}
