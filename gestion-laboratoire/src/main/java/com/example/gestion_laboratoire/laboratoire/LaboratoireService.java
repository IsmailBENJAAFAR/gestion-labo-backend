package com.example.gestion_laboratoire.laboratoire;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Laboratoire getLaboratoiresByIdLong(Long id) {
        return laboratoireRepository.findById(id).get();
    }

    public void createLaboratoire(Laboratoire laboratoire) {
        laboratoireRepository.save(laboratoire);
    }

    public void updateLaboratoire(Long id, Laboratoire laboratoire) {
        if (laboratoireRepository.existsById(id))
            laboratoireRepository.save(laboratoire);
    }

    public void deleteLaboratoire(Long id) {
        if (laboratoireRepository.existsById(id))
            laboratoireRepository.deleteById(id);
    }

    /*
     * TODO: Should implement:
     * getLaboratoire(id)
     * getLaboratoires()
     * createLaboratoire(Laboratoire??)
     * updateLaboratoire(Laboratoire??)
     * deleteLaboratoire(id)
     */
}
