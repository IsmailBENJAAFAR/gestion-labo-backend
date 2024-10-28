package com.example.gestion_laboratoire.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gestion_laboratoire.Models.Laboratoire;
import com.example.gestion_laboratoire.repositories.LaboratoireRepository;

@Service
public class LaboratoireService {
    // TODO: Will contain all the logic of CRUD here, maybe more stuff too

    private final LaboratoireRepository laboratoireRepository;

    @Autowired
    public LaboratoireService(LaboratoireRepository laboratoireRepository) {
        this.laboratoireRepository = laboratoireRepository;
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

    public void createLaboratoire(Laboratoire laboratoire) {
        laboratoireRepository.save(laboratoire);
    }

    @Transactional
    public void updateLaboratoire(Long id, Laboratoire laboratoire) {
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
                
        } catch (Exception e) {
            System.out.println(
                    "============================================Not found============================================");
        }
    }

    public void deleteLaboratoire(Long id) {
        if (laboratoireRepository.existsById(id))
            laboratoireRepository.deleteById(id);
    }

}
