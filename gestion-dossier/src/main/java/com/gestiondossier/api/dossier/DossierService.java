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

}
