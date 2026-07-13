package com.example.demo.Controller;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Service.AdminAcessService;
import com.example.demo.Service.ProfilService;
import com.example.demo.Service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AdminController {

    private final AdminAcessService adminAcessService;
    private final UtilisateurService utilisateurService;
    private final ProfilService profilService;

    // ========== GESTION DES PROFILS ==========

    // Lister tous les profils
    @GetMapping("/profils")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<List<Profil>> listerProfils() {
        return ResponseEntity.ok(profilService.listerTous());
    }

    // Créer un profil (avec nom)
    @PostMapping("/profils")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Profil> creerProfil(@RequestParam String nom) {
        Profil profil = profilService.creerProfil(nom, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(profil);
    }

    // Modifier un profil (nom)
    @PutMapping("/profils/{profilId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Profil> modifierProfil(@PathVariable Long profilId, @RequestParam String nouveauNom) {
        Profil profil = profilService.modifierProfil(profilId, nouveauNom);
        return ResponseEntity.ok(profil);
    }

    // Supprimer un profil
    @DeleteMapping("/profils/{profilId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> supprimerProfil(@PathVariable Long profilId) {
        profilService.supprimerProfil(profilId);
        return ResponseEntity.noContent().build();
    }

    // Ajouter une permission à un profil
    @PostMapping("/profils/{profilId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> ajouterPermissionAuProfil(@PathVariable Long profilId, @PathVariable Long permissionId) {
        profilService.ajouterPermissionAuProfil(profilId, permissionId);
        return ResponseEntity.ok().build();
    }

    // Retirer une permission d'un profil
    @DeleteMapping("/profils/{profilId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> retirerPermissionAuProfil(@PathVariable Long profilId, @PathVariable Long permissionId) {
        profilService.retirerPermissionDuProfil(profilId, permissionId);
        return ResponseEntity.ok().build();
    }

    // ========== GESTION DES PERMISSIONS (globales) ==========

    // Lister toutes les permissions
    @GetMapping("/permissions")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<List<Permission>> listerPermissions() {
        return ResponseEntity.ok(profilService.listerToutesLesPermissions());
    }

    // Modifier une permission (nom)
    @PutMapping("/permission/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Permission> modifierPermission(@PathVariable Long permissionId, @RequestParam String nouveaunom) {
        Permission permission = profilService.modifierPermission(permissionId, nouveaunom);
        return ResponseEntity.ok(permission);
    }

    // ========== GESTION DES UTILISATEURS ==========

    // Lister tous les utilisateurs
    @GetMapping("/utilisateurs")
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_VOIR')")
    public ResponseEntity<List<Utilisateur>> listerUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.listerTous());
    }

    // Modifier un utilisateur (champs partiels)
    @PutMapping("/utilisateurs/{utilisateurId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Utilisateur> modifierUtilisateur(
            @PathVariable Long utilisateurId,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) Long profilId) {
        Utilisateur user = utilisateurService.modifierUtilisateur(utilisateurId, nom, prenom, email, telephone, profilId);
        return ResponseEntity.ok(user);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/utilisateurs/{utilisateurId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long utilisateurId) {
        utilisateurService.supprimerUtilisateur(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    // Réinitialiser le mot de passe d'un utilisateur
    @PutMapping("/utilisateurs/{utilisateurId}/reset-password")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> resetPassword(@PathVariable Long utilisateurId, @RequestParam String newPassword) {
        utilisateurService.resetPassword(utilisateurId, newPassword);
        return ResponseEntity.ok().build();
    }

    // Ajouter une permission spécifique à un utilisateur
    @PostMapping("/utilisateurs/{utilisateurId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> accorderPermissionSupplementaire(@PathVariable Long utilisateurId, @PathVariable Long permissionId) {
        adminAcessService.accorderPermissionSupplementaire(utilisateurId, permissionId);
        return ResponseEntity.ok().build();
    }

    // Retirer une permission spécifique d'un utilisateur
    @DeleteMapping("/utilisateurs/{utilisateurId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> retirerPermissionSupplementaire(@PathVariable Long utilisateurId, @PathVariable Long permissionId) {
        adminAcessService.retirerPermissionSupplementaire(utilisateurId, permissionId);
        return ResponseEntity.ok().build();
    }
}