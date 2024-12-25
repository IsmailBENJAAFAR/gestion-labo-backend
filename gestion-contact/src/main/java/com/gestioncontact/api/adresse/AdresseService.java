package com.gestioncontact.api.adresse;

import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import com.gestioncontact.api.adresse.models.dto.CreateAdresseDTO;
import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdresseService {

    private final AdresseRepository adresseRepository;

    public AdresseDTO createAdresse(CreateAdresseDTO createAdresseDTO) {
        Adresse adresse = Adresse.builder()
                .numVoie(createAdresseDTO.getNumVoie())
                .nomVoie(createAdresseDTO.getNomVoie())
                .codePostal(createAdresseDTO.getCodePostal())
                .ville(createAdresseDTO.getVille())
                .commune(createAdresseDTO.getCommune())
                .build();

        Adresse savedAdresse = adresseRepository.save(adresse);
        return mapToDTO(savedAdresse);
    }

    public AdresseDTO getAdresseById(Integer id) {
        Adresse adresse = adresseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", "id", id));
        return mapToDTO(adresse);
    }

    public List<AdresseDTO> getAllAdresses() {
        List<Adresse> adresses = adresseRepository.findAll();
        return adresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AdresseDTO updateAdresse(Integer id, AdresseDTO adresseDTO) {
        Adresse adresse = adresseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", "id", id));

        adresse.setNumVoie(adresseDTO.getNumVoie());
        adresse.setNomVoie(adresseDTO.getNomVoie());
        adresse.setCodePostal(adresseDTO.getCodePostal());
        adresse.setVille(adresseDTO.getVille());
        adresse.setCommune(adresseDTO.getCommune());

        Adresse updatedAdresse = adresseRepository.save(adresse);
        return mapToDTO(updatedAdresse);
    }

    public void deleteAdresse(Integer id) {
        Adresse adresse = adresseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", "id", id));
        adresseRepository.delete(adresse);
    }

    private AdresseDTO mapToDTO(Adresse adresse) {
        AdresseDTO dto = new AdresseDTO();
        dto.setId(adresse.getId());
        dto.setNumVoie(adresse.getNumVoie());
        dto.setNomVoie(adresse.getNomVoie());
        dto.setCodePostal(adresse.getCodePostal());
        dto.setVille(adresse.getVille());
        dto.setCommune(adresse.getCommune());
        return dto;
    }
}
