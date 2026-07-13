package com.example.demo.Dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserMeDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private ProfilMeDto profil;
    private Set<PermissionMeDto> permissionsSpecifiques;
}


