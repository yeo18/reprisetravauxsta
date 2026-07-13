package com.example.demo.Controller;

import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Service.AdminAcessService;
import com.example.demo.Service.ProfilService;
import com.example.demo.Service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class AdminController {
    private  final AdminAcessService adminAcessService;
    private final UtilisateurService utilisateurService;
    private final ProfilService profilService;
    @PostMapping("/profil/{profilId}/permission/{permissionId}")
    public ResponseEntity<String> attribuerProfil(@PathVariable Long profilId, @PathVariable Long permissionId){
        adminAcessService.attribuerDroitProfil(profilId,permissionId);
        return ResponseEntity.ok("Profil ajoute au profil avec succes.");
    }
    @PutMapping("/profils/{profilId}")
    public ResponseEntity<String>modifierProfil(@PathVariable Long profilId, @RequestParam String nouveauNom){
        adminAcessService.modifierProfil(profilId,nouveauNom);
        return ResponseEntity.ok("profil modifier avec succes");
    }
    @DeleteMapping("/profils/{profilId}/permissions/{permissionId}")
    public ResponseEntity<String> deleteProfil(@PathVariable Long profilId, @PathVariable Long permissionId){
        adminAcessService.retirerDroitProfil(profilId,permissionId);
        return ResponseEntity.ok("profil supprimé");
    }

    @PostMapping("/utilisateurs/{utilisateurId}/permissions/{permissionId}")
    public ResponseEntity<String>accorderPermission(@PathVariable Long utilisateurId, @PathVariable Long permissionId){
        adminAcessService.accorderPermissionSupplementaire(utilisateurId,permissionId);
        return ResponseEntity.ok("Droit avec succes.");
    }
    @PutMapping("/permission/{permissionId}")
    public ResponseEntity<String>modifierPermission(@PathVariable Long permissionId, @RequestParam String nouveaunom){
        adminAcessService.modifierPermission(permissionId,nouveaunom);
        return ResponseEntity.ok("permission modifie");
    }
    @DeleteMapping("/utilisateurs/{utilisateurId}/permissions/{permissionsId}")
    public ResponseEntity <String>deletePermission(@PathVariable Long utilisateurId, @PathVariable Long permissionsId){
        adminAcessService.retirerPermissionSupplementaire(utilisateurId,permissionsId);
        return ResponseEntity.ok("Permission retiré  avec succes.");
    }
    @GetMapping("/profils")
    @PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
    public ResponseEntity<List<Profil>> listerProfils() {
        return ResponseEntity.ok(profilService.listerTous());
    }

}
