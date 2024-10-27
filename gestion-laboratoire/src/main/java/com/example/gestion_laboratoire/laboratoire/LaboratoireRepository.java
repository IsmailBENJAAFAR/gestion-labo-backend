package com.example.gestion_laboratoire.laboratoire;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> {
    // Part of the persistance data and the one that access the data in the db
}
