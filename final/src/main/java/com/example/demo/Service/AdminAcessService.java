package com.example.demo.Service;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.PermissionRepository;
import com.example.demo.Repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAcessService {

    private final UtilisateurRepository utilisateurRepository;
    private final PermissionRepository permissionRepository;

    // ========== Gestion des permissions spécifiques aux utilisateurs ==========

    @Transactional
    public void accorderPermissionSupplementaire(Long utilisateurId, Long permissionId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + utilisateurId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission introuvable avec l'ID : " + permissionId));
        utilisateur.getPermissionsSpecifiques().add(permission);
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void retirerPermissionSupplementaire(Long utilisateurId, Long permissionId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + utilisateurId));
        boolean removed = utilisateur.getPermissionsSpecifiques().removeIf(p -> p.getId().equals(permissionId));
        if (!removed) {
            throw new RuntimeException("Cette permission spécifique n'est pas attribuée à cet utilisateur");
        }
        utilisateurRepository.save(utilisateur);
    }

}