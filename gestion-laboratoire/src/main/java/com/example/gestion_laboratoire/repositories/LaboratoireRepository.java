package com.example.gestion_laboratoire.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gestion_laboratoire.models.Laboratoire;

public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> {
}
