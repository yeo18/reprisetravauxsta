package com.example.demo.Service;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.PermissionRepository;
import com.example.demo.Repository.ProfilRepository;
import com.example.demo.Repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAcessService {

    private final ProfilRepository profilRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PermissionRepository permissionRepository;

    // ========== Gestion des droits des profils ==========

    @Transactional
    public void attribuerDroitProfil(Long profilId, Long permissionId) {
        Profil profil = profilRepository.findById(profilId)
                .orElseThrow(() -> new RuntimeException("Profil introuvable avec l'ID : " + profilId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission introuvable avec l'ID : " + permissionId));
        profil.getPermissions().add(permission);
        // pas besoin de save explicitement car l'entité est gérée par la session (persistante)
        // mais pour forcer la mise à jour, on peut appeler save
        profilRepository.save(profil);
    }

    @Transactional
    public void retirerDroitProfil(Long profilId, Long permissionId) {
        Profil profil = profilRepository.findById(profilId)
                .orElseThrow(() -> new RuntimeException("Profil introuvable avec l'ID : " + profilId));
        boolean removed = profil.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        if (!removed) {
            throw new RuntimeException("Cette permission n'est pas associée à ce profil");
        }
        profilRepository.save(profil);
    }

    // ========== Gestion des permissions spécifiques aux utilisateurs ==========

    @Transactional
    public void accorderPermissionSupplementaire(Long utilisateurId, Long permissionId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + utilisateurId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission introuvable avec l'ID : " + permissionId));
        utilisateur.getPermissionsSpecifique().add(permission);
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void retirerPermissionSupplementaire(Long utilisateurId, Long permissionId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + utilisateurId));
        boolean removed = utilisateur.getPermissionsSpecifique().removeIf(p -> p.getId().equals(permissionId));
        if (!removed) {
            throw new RuntimeException("Cette permission spécifique n'est pas attribuée à cet utilisateur");
        }
        utilisateurRepository.save(utilisateur);
    }

    // ========== Gestion des permissions (nom) ==========

    @Transactional
    public void modifierPermission(Long permissionId, String nouveauNom) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission introuvable avec l'ID : " + permissionId));
        if (!permission.getNom().equals(nouveauNom)) {
            // Vérifier l'unicité du nouveau nom
            permissionRepository.findByNom(nouveauNom).ifPresent(p -> {
                throw new RuntimeException("Une permission avec le nom '" + nouveauNom + "' existe déjà");
            });
            permission.setNom(nouveauNom);
            permissionRepository.save(permission);
        }
        // Si le nom est identique, ne rien faire
    }
}