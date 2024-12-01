package com.gestiondossier.api.contactlaboratoire;

import com.gestiondossier.api.contactlaboratoire.models.entity.ContactLaboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactLaboratoireRepository extends JpaRepository<ContactLaboratoire, Integer> {
}
