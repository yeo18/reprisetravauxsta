package com.example.demo.Service;

import com.example.demo.Dto.Inscription;
import com.example.demo.Dto.PermissionMeDto;
import com.example.demo.Dto.ProfilMeDto;
import com.example.demo.Dto.UserMeDto;


import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.ProfilRepository;
import com.example.demo.Repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    private final ProfilRepository profilRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    public Utilisateur inscrire(Inscription inscription) {
        if (utilisateurRepository.findByEmail(inscription.getEmail()).isPresent()) {
            throw new RuntimeException("Vous etes deja inscrit");
        }
        Profil profilUser = profilRepository.findByNom("USER")
                .orElseThrow(() -> new RuntimeException("Profil USER manquant"));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(inscription.getNom());
        utilisateur.setPrenom(inscription.getPrenom());
        utilisateur.setEmail(inscription.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(inscription.getPassword()));
        utilisateur.setProfil(profilUser);
        return utilisateurRepository.save(utilisateur);
    }
    public Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utilisateur non trouver"));
    }

    public List<Utilisateur> listerTous() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
    // Dans UtilisateurService.java
    public UserMeDto getCurrentUserDto(String email) {
        Utilisateur utilisateur = trouverParEmail(email);
        return convertToDto(utilisateur);
    }

    private UserMeDto convertToDto(Utilisateur utilisateur) {
        UserMeDto dto = new UserMeDto();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelephone(utilisateur.getTelephone());

        if (utilisateur.getProfil() != null) {
            ProfilMeDto profilDto = new ProfilMeDto();
            profilDto.setId(utilisateur.getProfil().getId());
            profilDto.setNom(utilisateur.getProfil().getNom());

            Set<PermissionMeDto> permsDto = utilisateur.getProfil().getPermissions()
                    .stream()
                    .map(p -> {
                        PermissionMeDto pm = new PermissionMeDto();
                        pm.setId(p.getId());
                        pm.setNom(p.getNom());
                        return pm;
                    })
                    .collect(Collectors.toSet());
            profilDto.setPermissions(permsDto);
            dto.setProfil(profilDto);
        }

        Set<PermissionMeDto> specDto = utilisateur.getPermissionsSpecifique()
                .stream()
                .map(p -> {
                    PermissionMeDto pm = new PermissionMeDto();
                    pm.setId(p.getId());
                    pm.setNom(p.getNom());
                    return pm;
                })
                .collect(Collectors.toSet());
        dto.setPermissionsSpecifiques(specDto);

        return dto;
    }

}
