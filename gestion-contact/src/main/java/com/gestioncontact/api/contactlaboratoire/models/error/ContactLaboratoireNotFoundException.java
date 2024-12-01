package com.gestioncontact.api.contactlaboratoire.models.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContactLaboratoireNotFoundException extends RuntimeException {
}
