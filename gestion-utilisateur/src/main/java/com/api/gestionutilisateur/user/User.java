package com.api.gestionutilisateur.user;

import com.api.gestionutilisateur.laboratoire.Laboratoire;

public class User {
    private Integer id;
    private String email;
    private Laboratoire laboratoire;
    private String nomComplet;
    private Profession profession;
    private int numeroTelephone;
    private Role role;
}
