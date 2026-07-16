package com.example.demo.Controller;

import com.example.demo.Entity.Utilisateur;
import com.example.demo.Service.UtilisateurService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/utilisateurs")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // 0. Créer un utilisateur (admin)
    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_CREER')")
    public ResponseEntity<Utilisateur> creerUtilisateur(
            @RequestParam @Size(min = 2) String nom,
            @RequestParam @Size(min = 2) String prenom,
            @RequestParam String email,
            @RequestParam @Size(min = 8) String password,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) Long profilId) {
        Utilisateur user = utilisateurService.creerUtilisateurParAdmin(nom, prenom, email, password, telephone, profilId);
        return ResponseEntity.ok(user);
    }

    // 1. Lister tous les utilisateurs
    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_VOIR')")
    public ResponseEntity<List<Utilisateur>> listerUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.listerTous());
    }

    // 2. Voir un utilisateur par son ID
    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_VOIR')")
    public ResponseEntity<Utilisateur> getUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.trouverParId(id));
    }

    // 3. Modifier un utilisateur (admin : profil et permissions uniquement, pas d'infos personnelles)
    @PutMapping("/{utilisateurId}")
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_MODIFIER')")
    public ResponseEntity<Utilisateur> modifierUtilisateur(
            @PathVariable Long utilisateurId,
            @RequestParam(required = false) Long profilId) {
        Utilisateur user = utilisateurService.modifierUtilisateur(utilisateurId, null, null, null, null, profilId);
        return ResponseEntity.ok(user);
    }

    // 4. Supprimer un utilisateur
    @DeleteMapping("/{utilisateurId}")
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_SUPPRIMER')")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long utilisateurId) {
        utilisateurService.supprimerUtilisateur(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    // 5. Réinitialiser le mot de passe d'un utilisateur
    @PutMapping("/{utilisateurId}/reset-password")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long utilisateurId,
            @RequestParam @Size(min = 8) String newPassword) {
        utilisateurService.resetPassword(utilisateurId, newPassword);
        return ResponseEntity.ok().build();
    }
}