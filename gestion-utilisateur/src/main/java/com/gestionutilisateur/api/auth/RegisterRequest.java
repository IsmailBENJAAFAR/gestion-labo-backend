package com.gestionutilisateur.api.auth;

import com.gestionutilisateur.api.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String nomComplet;
    private String email;
    private String password;
    private Role role;
}
