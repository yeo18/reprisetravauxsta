package com.example.demo.Service;

import com.example.demo.Dto.Inscription;
import com.example.demo.Dto.PermissionMeDto;
import com.example.demo.Dto.ProfilMeDto;
import com.example.demo.Dto.UserMeDto;

import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.ProfilRepository;
import com.example.demo.Repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
            throw new RuntimeException("Vous êtes déjà inscrit");
        }
        Profil profilUser = profilRepository.findByNom("USER")
                .orElseThrow(() -> new RuntimeException("Profil USER manquant"));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(inscription.getNom());
        utilisateur.setPrenom(inscription.getPrenom());
        utilisateur.setEmail(inscription.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(inscription.getPassword()));
        utilisateur.setTelephone(inscription.getTelephone() != null ? inscription.getTelephone() : "");
        utilisateur.setProfil(profilUser);
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur creerUtilisateurParAdmin(String nom, String prenom, String email, String password, String telephone, Long profilId) {
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        Profil profil = profilId != null
                ? profilRepository.findById(profilId).orElseThrow(() -> new RuntimeException("Profil non trouvé"))
                : profilRepository.findByNom("USER").orElseThrow(() -> new RuntimeException("Profil USER manquant"));
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setPassword(passwordEncoder.encode(password));
        utilisateur.setTelephone(telephone != null ? telephone : "");
        utilisateur.setProfil(profil);
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<Utilisateur> listerTous() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public UserMeDto getCurrentUserDto(String email) {
        Utilisateur utilisateur = trouverParEmail(email);
        return convertToDto(utilisateur);
    }

    /**
     * Convertit un Utilisateur en UserMeDto pour l'API
     */
    public UserMeDto convertToDto(Utilisateur utilisateur) {
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

        Set<PermissionMeDto> specDto = utilisateur.getPermissionsSpecifiques()
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

    private void validerTelephone(String telephone) {
        if (telephone != null && !telephone.isBlank() && !telephone.matches("^[0-9]{10}$")) {
            throw new RuntimeException("Le téléphone doit contenir exactement 10 chiffres");
        }
    }

    private void validerEmail(String email) {
        if (email != null && !email.isBlank() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Format d'email invalide");
        }
    }

    @Transactional
    public Utilisateur modifierUtilisateur(Long id, String nom, String prenom, String email, String telephone, Long profilId) {
        Utilisateur user = trouverParId(id);
        if (nom != null) user.setNom(nom);
        if (prenom != null) user.setPrenom(prenom);
        if (email != null) {
            validerEmail(email);
            if (utilisateurRepository.findByEmail(email).isPresent() && !user.getEmail().equals(email)) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            user.setEmail(email);
        }
        if (telephone != null) {
            validerTelephone(telephone);
            user.setTelephone(telephone);
        }
        if (profilId != null) {
            Profil profil = profilRepository.findById(profilId)
                    .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
            user.setProfil(profil);
        }
        user.setDateModification(LocalDateTime.now());
        return utilisateurRepository.save(user);
    }

    @Transactional
    public Utilisateur modifierMonProfil(Long id, String nom, String prenom, String email, String telephone, String password) {
        Utilisateur user = trouverParId(id);
        if (nom != null) user.setNom(nom);
        if (prenom != null) user.setPrenom(prenom);
        if (email != null) {
            validerEmail(email);
            if (utilisateurRepository.findByEmail(email).isPresent() && !user.getEmail().equals(email)) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            user.setEmail(email);
        }
        if (telephone != null) {
            validerTelephone(telephone);
            user.setTelephone(telephone);
        }
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setDateModification(LocalDateTime.now());
        return utilisateurRepository.save(user);
    }

    @Transactional
    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    @Transactional
    public void resetPassword(Long id, String nouveauMotDePasse) {
        Utilisateur user = trouverParId(id);
        user.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(user);
    }
}