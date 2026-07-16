package com.example.demo.Controller;

import com.example.demo.Dto.ChantierDto;
import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Mestypes.StatusChantier;
import com.example.demo.Entity.Tache;
import com.example.demo.Service.ChantierService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/admin/chantier")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Chantiers", description = "Gestion des chantiers")
public class ChantierController {
    private final ChantierService chantierService;

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_CREER')")
    @Operation(summary = "Créer un chantier", description = "Crée un nouveau chantier")
    public ResponseEntity<?> creerChantier(@RequestBody @Valid ChantierDto dto) {
        Chantier chantier = chantierService.creerChantier(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Chantier créé avec succès", "data", chantier));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    @Operation(summary = "Modifier un chantier", description = "Met à jour un chantier existant")
    public ResponseEntity<?> modifierChantier(@PathVariable Long id, @Valid @RequestBody ChantierDto dto) {
        Chantier chantier = chantierService.modifierChantier(id, dto);
        return ResponseEntity.ok(Map.of("message", "Chantier modifié avec succès", "data", chantier));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_SUPPRIMER')")
    @Operation(summary = "Supprimer un chantier", description = "Supprime un chantier")
    public ResponseEntity<?> supprimerChantier(@PathVariable Long id) {
        chantierService.supprimerChantier(id);
        return ResponseEntity.ok(Map.of("message", "Chantier supprimé avec succès"));
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    @Operation(summary = "Liste des chantiers", description = "Retourne tous les chantiers")
    public ResponseEntity<?> voirListeChantier() {
        return ResponseEntity.ok(chantierService.voirListeChantier());
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR_STATS')")
    @Operation(summary = "Statistiques d'un chantier", description = "Retourne les statistiques d'un chantier")
    public ResponseEntity<Map<String, Object>> voirStats(@PathVariable Long id) {
        Map<String, Object> stats = chantierService.voirStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    @Operation(summary = "Détail d'un chantier", description = "Retourne un chantier par son ID")
    public ResponseEntity<Chantier> SelectionnerUnChantier(@PathVariable Long id) {
        return ResponseEntity.ok(chantierService.SelectionnerUnChantier(id));
    }

    @GetMapping("/{id}/urgentes")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    @Operation(summary = "Tâches urgentes d'un chantier", description = "Liste les tâches urgentes d'un chantier")
    public ResponseEntity<List<Tache>> TachesUrgentesParChantier(@PathVariable Long id) {
        return ResponseEntity.ok(chantierService.TachesUrgentesParChantier(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    @Operation(summary = "Changer le statut", description = "Modifie le statut d'un chantier")
    public ResponseEntity<?> changerStatus(@PathVariable Long id, @RequestParam StatusChantier status) {
        Chantier chantier = chantierService.changerStatus(id, status);
        return ResponseEntity.ok(Map.of("message", "Statut modifié avec succès", "data", chantier));
    }

    @GetMapping("/mon-chantier")
    @Operation(summary = "Mon chantier", description = "Retourne le chantier de l'utilisateur connecté")
    public ResponseEntity<Chantier> monChantier() {
        return ResponseEntity.ok(chantierService.monChantier());
    }
}
