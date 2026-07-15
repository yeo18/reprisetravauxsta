package com.example.demo.Controller;

import com.example.demo.Dto.TacheDto;
import com.example.demo.Entity.Tache;
import com.example.demo.Service.TacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/taches")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TacheController {
    private final TacheService tacheService;

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_CREER')")
    public ResponseEntity<?> creerTache(@RequestBody TacheDto dto) {
        Tache tache = tacheService.creerTache(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tâche créée avec succès", "data", tache));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_MODIFIER')")
    public ResponseEntity<?> updateTache(@PathVariable Long id, @RequestBody TacheDto dto) {
        Tache tache = tacheService.modifierTache(id, dto);
        return ResponseEntity.ok(Map.of("message", "Tâche modifiée avec succès", "data", tache));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_SUPPRIMER')")
    public ResponseEntity<?> supprimerTache(@PathVariable Long id) {
        tacheService.supprimerTache(id);
        return ResponseEntity.ok(Map.of("message", "Tâche supprimée avec succès"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public ResponseEntity<Tache> TacheParId(@PathVariable Long id) {
        return ResponseEntity.ok(tacheService.TacheParId(id));
    }

    @GetMapping("/chantier/{chantierId}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public ResponseEntity<List<Tache>> TacheParChantier(@PathVariable Long chantierId) {
        return ResponseEntity.ok(tacheService.TacheParChantier(chantierId));
    }

    @PostMapping("/{tacheId}/aasigner-equipe/{equipeId}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_ASSIGNER_EQUIPE')")
    public ResponseEntity<?> assignerTacheEquipe(@PathVariable Long tacheId, @PathVariable Long equipeId) {
        tacheService.assignerTacheAEquipe(tacheId, equipeId);
        return ResponseEntity.ok(Map.of("message", "Tâche assignée à l'équipe avec succès"));
    }

    @PostMapping("/{tacheId}/assigner-utilisateur/{utilisateurId}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_ASSIGNER_UTILISATEUR')")
    public ResponseEntity<?> assignerTacheAUtilisateur(@PathVariable Long tacheId, @PathVariable Long utilisateurId) {
        tacheService.assignerTacheAUtilisateur(tacheId, utilisateurId);
        return ResponseEntity.ok(Map.of("message", "Tâche assignée à l'utilisateur avec succès"));
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public ResponseEntity<List<Tache>> voirToutesLesTaches() {
        return ResponseEntity.ok(tacheService.getAllTaches());
    }

    @GetMapping("/mes-taches")
    public ResponseEntity<List<Tache>> mesTaches() {
        return ResponseEntity.ok(tacheService.mesTaches());
    }

    @PutMapping("/{id}/terminer")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VALIDER')")
    public ResponseEntity<?> terminerTache(@PathVariable Long id) {
        tacheService.terminerTache(id);
        return ResponseEntity.ok(Map.of("message", "Tâche validée avec succès"));
    }
}
