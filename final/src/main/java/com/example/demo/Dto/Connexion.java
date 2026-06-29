package com.example.demo.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Connexion {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @Data  public class ReponseAuthentification{
        private String token;
    }
    @Data  public class AssignationDePermission{
        @NotNull private String IdCible;
        @NotNull private String IdPermission;
    }
}
