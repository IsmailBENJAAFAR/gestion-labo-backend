package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DossierService {
    private final DossierRepository dossierRepository;

    public List<Dossier> findAll(){
        return dossierRepository.findAll();
    }

}
