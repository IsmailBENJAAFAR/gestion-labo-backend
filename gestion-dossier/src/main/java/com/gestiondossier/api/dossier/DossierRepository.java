package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierRepository extends JpaRepository<Dossier, Integer> {
}
