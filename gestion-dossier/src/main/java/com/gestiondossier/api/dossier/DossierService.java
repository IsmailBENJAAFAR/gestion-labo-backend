package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import com.gestiondossier.api.dossier.models.error.DossierNotFoundException;
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
        if (dossier.isPresent())
            return dossier.get();
        else
            throw new DossierNotFoundException();

    }

    public Dossier createDossier(Dossier dossier) {
        return dossierRepository.save(dossier);
    }

    @Transactional
    public Dossier updateDossier(Integer id, Dossier requestDossier) {
        Optional<Dossier> dossier = dossierRepository.findById(id);
        if (dossier.isPresent()) {
            dossier.get().setFkEmailUtilisateur(requestDossier.getFkEmailUtilisateur());
            dossier.get().setDate(requestDossier.getDate());
            dossier.get().setPatient(requestDossier.getPatient());
            return dossier.get();
        } else throw new DossierNotFoundException();

    }

    public void deleteDossier(Integer dossierId) {
        boolean exists = dossierRepository.existsById(dossierId);
        if (!exists) {
            throw new DossierNotFoundException();
        }
        dossierRepository.deleteById(dossierId);
    }
}
