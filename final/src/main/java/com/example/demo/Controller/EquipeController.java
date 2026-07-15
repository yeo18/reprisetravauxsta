package com.example.demo.Controller;

import com.example.demo.Dto.EquipeDto;
import com.example.demo.Entity.Equipe;
import com.example.demo.Service.EquipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/equipe")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class EquipeController {
    private final EquipeService equipeService;

    @PostMapping
    public ResponseEntity<?> creerEquipe(@RequestBody EquipeDto dto) {
        Equipe equipe = equipeService.creerEquipe(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Équipe créée avec succès", "data", equipe));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierEquipe(@PathVariable Long id, @RequestBody EquipeDto dto) {
        Equipe equipe = equipeService.modifierEquipe(id, dto);
        return ResponseEntity.ok(Map.of("message", "Équipe modifiée avec succès", "data", equipe));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> supprimerEquipe(@PathVariable Long id) {
        equipeService.supprimerEquipe(id);
        return ResponseEntity.ok(Map.of("message", "Équipe supprimée avec succès"));

    }

    @PostMapping("/{equipeId}/membres/{utilisateurId}")
    public ResponseEntity<?> ajouterMembre(@PathVariable Long equipeId, @PathVariable Long utilisateurId) {
        equipeService.ajouterMembre(equipeId, utilisateurId);
        return ResponseEntity.ok(Map.of("message", "Membre ajouté à l'équipe avec succès"));
    }

    @GetMapping("/{id}/nb-membres")
    public ResponseEntity<Integer> getNombreMembres(@PathVariable Long id) {
        int nb = equipeService.calculerNombreMembres(id);
        return ResponseEntity.ok(nb);
    }

    @DeleteMapping("/{equipeId}/membres/{utilisateurId}")
    public ResponseEntity<?> retirerMembre(@PathVariable Long equipeId, @PathVariable Long utilisateurId) {
        equipeService.retirerMembre(equipeId, utilisateurId);
        return ResponseEntity.ok(Map.of("message", "Membre retiré de l'équipe avec succès"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Equipe> EquipeUnique(@PathVariable Long id) {
        return ResponseEntity.ok(equipeService.EquipeUnique(id));
    }

    @GetMapping
    public ResponseEntity<java.util.List<Equipe>> voirListeEquipes() {
        return ResponseEntity.ok(equipeService.ListeEquipes());
    }
}

