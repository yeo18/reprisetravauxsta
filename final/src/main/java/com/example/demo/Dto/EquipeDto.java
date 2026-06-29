package com.example.demo.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipeDto {
    @NotBlank
    private String nom;
    private Long chantierId;
}
