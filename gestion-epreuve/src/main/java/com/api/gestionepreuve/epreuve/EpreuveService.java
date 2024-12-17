package com.api.gestionepreuve.epreuve;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EpreuveService {
    private final EpreuveRepository epreuveRepository;

    public List<Epreuve> getAllEpreuve() {
        return epreuveRepository.findAll();
    }
}
