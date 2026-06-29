package com.example.demo.Controller;

import com.example.demo.Entity.Utilisateur;
import com.example.demo.Service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // Lister tous les utilisateurs
    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_VOIR')")
    public ResponseEntity<List<Utilisateur>> listerUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.listerTous());
    }

    // Voir un utilisateur par son ID
    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('UTILISATEUR_VOIR')")
    public ResponseEntity<Utilisateur> getUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.trouverParId(id));
    }
}