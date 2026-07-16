package com.example.demo.Controller;

import com.example.demo.Dto.TacheDto;
import com.example.demo.Entity.Tache;
import com.example.demo.Service.TacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Tâches (Admin)", description = "Gestion complète des tâches par l'administrateur")
public class TacheController {
    private final TacheService tacheService;

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_CREER')")
    @Operation(summary = "Créer une tâche", description = "Crée une nouvelle tâche avec les informations fournies")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tâche créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<?> creerTache(@Valid @RequestBody TacheDto dto) {
        Tache tache = tacheService.creerTache(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tâche créée avec succès", "data", tache));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_MODIFIER')")
    @Operation(summary = "Modifier une tâche", description = "Met à jour une tâche existante")
    public ResponseEntity<?> updateTache(@PathVariable Long id, @Valid @RequestBody TacheDto dto) {
        Tache tache = tacheService.modifierTache(id, dto);
        return ResponseEntity.ok(Map.of("message", "Tâche modifiée avec succès", "data", tache));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_SUPPRIMER')")
    @Operation(summary = "Supprimer une tâche", description = "Supprime une tâche définitivement")
    public ResponseEntity<?> supprimerTache(@PathVariable Long id) {
        tacheService.supprimerTache(id);
        return ResponseEntity.ok(Map.of("message", "Tâche supprimée avec succès"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    @Operation(summary = "Détail d'une tâche", description = "Retourne une tâche par son ID")
    public ResponseEntity<Tache> TacheParId(@PathVariable Long id) {
        return ResponseEntity.ok(tacheService.TacheParId(id));
    }

    @GetMapping("/chantier/{chantierId}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    @Operation(summary = "Tâches par chantier", description = "Liste toutes les tâches d'un chantier")
    public ResponseEntity<List<Tache>> TacheParChantier(@PathVariable Long chantierId) {
        return ResponseEntity.ok(tacheService.TacheParChantier(chantierId));
    }

    @PostMapping("/{tacheId}/assigner-equipe/{equipeId}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_ASSIGNER_EQUIPE')")
    @Operation(summary = "Assigner une tâche à une équipe", description = "Associe une tâche à une équipe")
    public ResponseEntity<?> assignerTacheEquipe(@PathVariable Long tacheId, @PathVariable Long equipeId) {
        tacheService.assignerTacheAEquipe(tacheId, equipeId);
        return ResponseEntity.ok(Map.of("message", "Tâche assignée à l'équipe avec succès"));
    }

    @PostMapping("/{tacheId}/assigner-utilisateur/{utilisateurId}")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_ASSIGNER_UTILISATEUR')")
    @Operation(summary = "Assigner une tâche à un utilisateur", description = "Associe une tâche à un utilisateur")
    public ResponseEntity<?> assignerTacheAUtilisateur(@PathVariable Long tacheId, @PathVariable Long utilisateurId) {
        tacheService.assignerTacheAUtilisateur(tacheId, utilisateurId);
        return ResponseEntity.ok(Map.of("message", "Tâche assignée à l'utilisateur avec succès"));
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    @Operation(summary = "Liste toutes les tâches", description = "Retourne toutes les tâches (admin uniquement)")
    public ResponseEntity<List<Tache>> voirToutesLesTaches() {
        return ResponseEntity.ok(tacheService.getAllTaches());
    }

    @PutMapping("/{id}/terminer")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VALIDER')")
    @Operation(summary = "Valider une tâche", description = "Marque une tâche comme terminée")
    public ResponseEntity<?> terminerTache(@PathVariable Long id) {
        tacheService.terminerTache(id);
        return ResponseEntity.ok(Map.of("message", "Tâche validée avec succès"));
    }
}
