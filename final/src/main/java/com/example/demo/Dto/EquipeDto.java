package com.example.demo.Dto;

import com.example.demo.util.HtmlSanitizer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EquipeDto {
    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @Size(max = 255, message = "Le domaine ne doit pas dépasser 255 caractères")
    private String domaine;

    @Positive(message = "L'ID du chantier doit être positif")
    private Long chantierId;

    public void setNom(String nom) { this.nom = HtmlSanitizer.sanitize(nom); }
    public void setDomaine(String domaine) { this.domaine = HtmlSanitizer.sanitize(domaine); }
}
