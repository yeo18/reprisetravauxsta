package com.example.demo.Dto;

import com.example.demo.Entity.Mestypes.StatusChantier;
import com.example.demo.Entity.Mestypes.TypeChantier;
import com.example.demo.util.HtmlSanitizer;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ChantierDto {
    @NotBlank(message = "Le nom du chantier est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotNull(message = "Le type de chantier est obligatoire")
    private TypeChantier type;

    @NotBlank(message = "La localisation est obligatoire")
    @Size(max = 255, message = "La localisation ne doit pas dépasser 255 caractères")
    private String localisation;

    @Size(max = 50, message = "La longitude ne doit pas dépasser 50 caractères")
    @Pattern(regexp = "^[-+]?\\d{1,3}\\.?\\d{0,10}$", message = "Format de longitude invalide")
    private String longitude;

    @Size(max = 50, message = "La latitude ne doit pas dépasser 50 caractères")
    @Pattern(regexp = "^[-+]?\\d{1,3}\\.?\\d{0,10}$", message = "Format de latitude invalide")
    private String latitude;

    private String photo;

    private StatusChantier status;

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    private LocalDate datedebut;

    @FutureOrPresent(message = "La date de fin ne peut pas être dans le passé")
    private LocalDate datefin;

    @AssertTrue(message = "La date de fin doit être postérieure ou égale à la date de début")
    public boolean isDatefinValid() {
        if (datedebut == null || datefin == null) return true;
        return !datefin.isBefore(datedebut);
    }

    public void setNom(String nom) { this.nom = HtmlSanitizer.sanitize(nom); }
    public void setLocalisation(String localisation) { this.localisation = HtmlSanitizer.sanitize(localisation); }
    public void setLongitude(String longitude) { this.longitude = HtmlSanitizer.sanitize(longitude); }
    public void setLatitude(String latitude) { this.latitude = HtmlSanitizer.sanitize(latitude); }
}
