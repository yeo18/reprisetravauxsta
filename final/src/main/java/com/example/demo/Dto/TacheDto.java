package com.example.demo.Dto;

import com.example.demo.Entity.Mestypes.Priorite;
import com.example.demo.Entity.Mestypes.StatusTache;
import com.example.demo.util.HtmlSanitizer;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class TacheDto {

    @NotBlank(message = "Le titre de la tâche est obligatoire")
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
    private String titre;

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;

    @DecimalMin(value = "0.0", message = "Le coût estimé ne peut pas être négatif")
    @DecimalMax(value = "999999999.99", message = "Le coût estimé est trop élevé")
    private Double coutEstime;

    @Min(value = 0, message = "Les années ne peuvent pas être négatives")
    @Max(value = 100, message = "Les années ne peuvent pas dépasser 100")
    private Integer dureeEstimeeAnnees = 0;

    @Min(value = 0, message = "Les mois ne peuvent pas être négatifs")
    @Max(value = 11, message = "Les mois doivent être entre 0 et 11")
    private Integer dureeEstimeeMois = 0;

    @Min(value = 0, message = "Les jours ne peuvent pas être négatifs")
    @Max(value = 31, message = "Les jours doivent être entre 0 et 31")
    private Integer dureeEstimeeJours = 0;

    @NotNull(message = "La priorité est obligatoire")
    private Priorite priorite;

    @NotNull(message = "Le statut est obligatoire")
    private StatusTache status;

    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    private LocalDate datedebut;

    @FutureOrPresent(message = "La date de fin ne peut pas être dans le passé")
    private LocalDate datefin;

    @NotNull(message = "L'ID du chantier est obligatoire")
    @Positive(message = "L'ID du chantier doit être positif")
    private Long chantierId;

    @Positive(message = "L'ID de l'utilisateur doit être positif")
    private Long utilisateurId;

    @Positive(message = "L'ID de l'équipe doit être positif")
    private Long equipeId;

    @AssertTrue(message = "La date de fin doit être postérieure ou égale à la date de début")
    public boolean isDatefinValid() {
        if (datedebut == null || datefin == null) return true;
        return !datefin.isBefore(datedebut);
    }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = HtmlSanitizer.sanitize(titre); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = HtmlSanitizer.sanitize(description); }
}
