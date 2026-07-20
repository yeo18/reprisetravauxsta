package com.example.demo.Controller;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Service.AdminAcessService;
import com.example.demo.Service.PermissionService;
import com.example.demo.Service.ProfilService;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import com.example.demo.Dto.PermissionCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    public ResponseEntity<Void> retirerPermissionDuProfil(@PathVariable Long profilId, @PathVariable Long permissionId) {
        profilService.retirerPermissionDuProfil(profilId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/permission/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Permission> modifierPermission(@PathVariable Long permissionId, @RequestParam String nouveaunom) {
        Permission permission = permissionService.modifierPermission(permissionId, nouveaunom);
        return ResponseEntity.ok(permission);
    }

    @PutMapping("/profils/{profilId}/permissions")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<Void> remplacerPermissionsDuProfil(@PathVariable Long profilId, @RequestBody List<Long> permissionIds) {
        profilService.remplacerPermissionsDuProfil(profilId, permissionIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permissions")
    @PreAuthorize("@securityEvaluator.hasPermission('PERMISSION_CREER')")
    public ResponseEntity<Permission> creerPermission(@Valid @RequestBody PermissionCreateDto dto) {
        Permission permission = permissionService.creerPermission(dto.getNom(), dto.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @DeleteMapping("/permissions/{permissionId}")
    @PreAuthorize("@securityEvaluator.hasPermission('PERMISSION_SUPPRIMER')")
    public ResponseEntity<Void> supprimerPermission(@PathVariable Long permissionId) {
        permissionService.supprimerPermission(permissionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<List<Permission>> listerPermissions() {
        List<Permission> perms = permissionService.listerToutes();
        System.out.println(">>> listerPermissions: found " + (perms != null ? perms.size() : "null") + " permissions");
        return ResponseEntity.ok(perms);
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