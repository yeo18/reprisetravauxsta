package com.example.demo.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionCreateDto {
    @NotBlank(message = "Le nom de la permission est requis")
    @Size(min = 3, max = 75, message = "Le nom doit contenir entre 3 et 75 caractères")
    private String nom;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;
}
