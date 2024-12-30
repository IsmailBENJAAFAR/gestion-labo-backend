package com.api.gestion_analyse.repositores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.gestion_analyse.models.Analyse;

@Repository
public interface AnalyseRepository extends JpaRepository<Analyse, Long> {
}
