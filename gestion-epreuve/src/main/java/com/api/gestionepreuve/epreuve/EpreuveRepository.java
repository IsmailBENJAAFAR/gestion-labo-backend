package com.api.gestionepreuve.epreuve;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpreuveRepository extends JpaRepository<Epreuve, Integer> {
}
