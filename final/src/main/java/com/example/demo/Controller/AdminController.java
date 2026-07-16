package com.example.demo.Controller;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Service.AdminAcessService;
import com.example.demo.Service.PermissionService;
import com.example.demo.Service.ProfilService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class AdminController {

    private final AdminAcessService adminAcessService;
    private final ProfilService profilService;
    private final PermissionService permissionService;

    // ========== PROFILS ==========
    @GetMapping("/profils")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<List<Profil>> listerProfils() {
        return ResponseEntity.ok(profilService.listerTous());
    }

    @PostMapping("/profils")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Profil> creerProfil(@RequestParam @Size(min = 2, max = 50) String nom) {
        Profil profil = profilService.creerProfil(com.example.demo.util.HtmlSanitizer.sanitize(nom), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(profil);
    }

    @PutMapping("/profils/{profilId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Profil> modifierProfil(@PathVariable Long profilId, @RequestParam @Size(min = 2, max = 50) String nouveauNom) {
        Profil profil = profilService.modifierProfil(profilId, nouveauNom);
        return ResponseEntity.ok(profil);
    }

    @DeleteMapping("/profils/{profilId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> supprimerProfil(@PathVariable Long profilId) {
        profilService.supprimerProfil(profilId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/profils/{profilId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> ajouterPermissionAuProfil(@PathVariable Long profilId, @PathVariable Long permissionId) {
        profilService.ajouterPermissionAuProfil(profilId, permissionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/profils/{profilId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> retirerPermissionAuProfil(@PathVariable Long profilId, @PathVariable Long permissionId) {
        profilService.retirerPermissionDuProfil(profilId, permissionId);
        return ResponseEntity.ok().build();
    }

    // ========== PERMISSIONS GLOBALES ==========
    @GetMapping("/permissions")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<List<Permission>> listerPermissions() {
        return ResponseEntity.ok(permissionService.listerToutes());
    }

    @PutMapping("/permission/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Permission> modifierPermission(@PathVariable Long permissionId, @RequestParam String nouveaunom) {
        Permission permission = permissionService.modifierPermission(permissionId, nouveaunom);
        return ResponseEntity.ok(permission);
    }

    // ========== PERMISSIONS SPÉCIFIQUES AUX UTILISATEURS ==========
    @PostMapping("/utilisateurs/{utilisateurId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> accorderPermissionSupplementaire(@PathVariable Long utilisateurId, @PathVariable Long permissionId) {
        adminAcessService.accorderPermissionSupplementaire(utilisateurId, permissionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/utilisateurs/{utilisateurId}/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> retirerPermissionSupplementaire(@PathVariable Long utilisateurId, @PathVariable Long permissionId) {
        adminAcessService.retirerPermissionSupplementaire(utilisateurId, permissionId);
        return ResponseEntity.ok().build();
    }
}