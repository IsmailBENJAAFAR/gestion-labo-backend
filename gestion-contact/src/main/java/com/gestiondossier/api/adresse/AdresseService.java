package com.gestiondossier.api.adresse;

import com.gestiondossier.api.adresse.models.entity.Adresse;
import com.gestiondossier.api.adresse.models.error.AdresseNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdresseService {
    private final AdresseRepository adresseRepository;

    public List<Adresse> findAll() {
        return adresseRepository.findAll();
    }

    public Adresse findById(Integer id) {
        return adresseRepository.findById(id)
                .orElseThrow(AdresseNotFoundException::new);
    }

    public Adresse createAdresse(Adresse adresse) {
        return adresseRepository.save(adresse);
    }

    @Transactional
    public Adresse updateAdresse(Integer id, Adresse requestAdresse) {
        Optional<Adresse> adresse = adresseRepository.findById(id);
        if (adresse.isPresent()) {
            Adresse existingAdresse = findById(id);
            existingAdresse.setNumVoie(requestAdresse.getNumVoie());
            existingAdresse.setNomVoie(requestAdresse.getNomVoie());
            existingAdresse.setCodePostal(requestAdresse.getCodePostal());
            existingAdresse.setVille(requestAdresse.getVille());
            existingAdresse.setCommune(requestAdresse.getCommune());
            return existingAdresse;
        } else throw new AdresseNotFoundException();
    }

    public void deleteAdresse(Integer adresseId) {
        if (!adresseRepository.existsById(adresseId)) {
            throw new AdresseNotFoundException();
        }
        adresseRepository.deleteById(adresseId);
    }
}
