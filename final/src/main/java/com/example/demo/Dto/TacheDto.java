package com.example.demo.Dto;

import com.example.demo.Entity.Mestypes.Priorite;
import com.example.demo.Entity.Mestypes.StatusTache;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class TacheDto {

    @NotBlank(message = "Le titre de la tâche est obligatoire")
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
    private String titre;

    private String description;

    @DecimalMin(value = "0.0", message = "Le coût estimé ne peut pas être négatif")
    private Double coutEstime;

    // --- Durée estimée éclatée ---
    @Min(value = 0, message = "Les années ne peuvent pas être négatives")
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

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate datedebut;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate datefin;

    @NotNull(message = "L'ID du chantier est obligatoire")
    private Long chantierId;

    private Long utilisateurId;

    private Long equipeId;
}
