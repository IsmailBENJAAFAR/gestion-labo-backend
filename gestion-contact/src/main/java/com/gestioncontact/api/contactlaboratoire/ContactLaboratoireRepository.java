package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.contactlaboratoire.models.entity.ContactLaboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactLaboratoireRepository extends JpaRepository<ContactLaboratoire, Integer> {
}
