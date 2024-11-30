package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DossierService {
    private final DossierRepository dossierRepository;

    public List<Dossier> findAll() {
        return dossierRepository.findAll();
    }

    public Dossier findById(Integer id) {
        Optional<Dossier> dossier = dossierRepository.findById(id);
        if (!dossier.isPresent())
            throw new IllegalStateException("dossier with id " + id + " does not exists");

        return dossier.get();
    }

    public Dossier createDossier(Dossier dossier) {
        return dossierRepository.save(dossier);
    }

    @Transactional
    public Dossier updateDossier(Dossier requestDossier) {
        Optional<Dossier> dossier = dossierRepository.findById(requestDossier.getId());
        if (!dossier.isPresent())
            throw new IllegalStateException("dossier with id " + requestDossier.getId() + " does not exists");

        dossier.get().setFkEmailUtilisateur(requestDossier.getFkEmailUtilisateur());
        dossier.get().setDate(requestDossier.getDate());
        dossier.get().setPatient(requestDossier.getPatient());

        return dossier.get();
    }

    public void deleteDossier(Integer dossierId) {
        boolean exists = dossierRepository.existsById(dossierId);
        if (!exists) {
            throw new IllegalStateException("Dossier with id " + dossierId + " does not exists");
        }
        dossierRepository.deleteById(dossierId);
    }
}
