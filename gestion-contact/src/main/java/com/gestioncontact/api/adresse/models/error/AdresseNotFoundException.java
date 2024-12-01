package com.gestioncontact.api.adresse.models.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdresseNotFoundException extends RuntimeException {
}
