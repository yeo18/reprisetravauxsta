package com.example.demo.Dto;

import com.example.demo.Entity.Mestypes.TypeChantier;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private String localisation;

    private String longitude;
    private String latitude;

    private String photo;
    @NotNull(message = "La date de début est obligatoire")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate datedebut;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate datefin;
}
