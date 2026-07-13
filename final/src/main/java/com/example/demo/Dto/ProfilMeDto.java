package com.example.demo.Dto;

import lombok.Data;

import java.util.Set;

@Data
public class ProfilMeDto {
    private Long id;
    private String nom;
    private Set<PermissionMeDto> permissions;
}