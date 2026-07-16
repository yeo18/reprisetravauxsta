package com.example.demo.Dto;

import com.example.demo.util.HtmlSanitizer;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Inscription {
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 40, message = "Le nom doit contenir entre 2 et 40 caractères")
    private String nom;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 75, message = "Le prénom doit contenir entre 2 et 75 caractères")
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    @Size(max = 75, message = "L'email ne doit pas dépasser 75 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern.List({
        @Pattern(regexp = ".*[A-Z].*", message = "Le mot de passe doit contenir au moins une majuscule"),
        @Pattern(regexp = ".*[a-z].*", message = "Le mot de passe doit contenir au moins une minuscule"),
        @Pattern(regexp = ".*\\d.*", message = "Le mot de passe doit contenir au moins un chiffre")
    })
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Le téléphone doit contenir exactement 10 chiffres")
    private String telephone;

    public void setNom(String nom) { this.nom = HtmlSanitizer.sanitize(nom); }
    public void setPrenom(String prenom) { this.prenom = HtmlSanitizer.sanitize(prenom); }
    public void setEmail(String email) { this.email = HtmlSanitizer.sanitize(email); }
}
