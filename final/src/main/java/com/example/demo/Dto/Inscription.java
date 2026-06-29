package com.example.demo.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Inscription {
    @NotBlank(message = "le nom ne peut pas etre vide")
    private String nom;
    @NotBlank(message = "le prenom ne peut pas etre vide")
    private String prenom;
    @NotBlank(message = "l'email est requis")
    @Email(message = "l'email doit etre valide")
    private String email;
    @NotBlank(message = "le mot de passe est requis")
//    @Size(min = 8,message = "le mot de passe doit contenir au moins 8 caractères ")
    private String password;
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Le téléphone doit contenir 10 chiffres"
    )
    private String telephone;

}
