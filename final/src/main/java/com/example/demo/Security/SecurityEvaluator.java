package com.example.demo.Security;

import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.UtilisateurRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("securityEvaluator")
public class SecurityEvaluator {

    private final UtilisateurRepository utilisateurRepository;

    public SecurityEvaluator(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(String permissionRequise) {
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailConnecte).orElse(null);
        if (utilisateur == null) return false;

        // Super admin : si profil nom = "ADMIN"
        if (utilisateur.getProfil() != null && "ADMIN".equalsIgnoreCase(utilisateur.getProfil().getNom())) {
            return true;
        }

        // Vérifier dans les permissions du profil
        boolean possedeViaRole = utilisateur.getProfil() != null &&
                utilisateur.getProfil().getPermissions().stream()
                        .anyMatch(p -> p.getNom().equalsIgnoreCase(permissionRequise));

        // Vérifier dans les permissions spécifiques
        boolean possedeViaSurcharge = utilisateur.getPermissionsSpecifique().stream()
                .anyMatch(p -> p.getNom().equalsIgnoreCase(permissionRequise));

        return possedeViaRole || possedeViaSurcharge;
    }
}