package com.example.demo.Controller;

import com.example.demo.Dto.ChantierDto;
import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Tache;
import com.example.demo.Service.ChantierService;
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
public class ChantierController {
    private final ChantierService chantierService;

    @PostMapping
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_CREER')")
    public ResponseEntity<?> creerChantier(@RequestBody @Valid ChantierDto dto) {
        Chantier chantier = chantierService.creerChantier(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Chantier créé avec succès", "data", chantier));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    public ResponseEntity<?> modifierChantier(@PathVariable Long id, @Valid @RequestBody ChantierDto dto) {
        Chantier chantier = chantierService.modifierChantier(id, dto);
        return ResponseEntity.ok(Map.of("message", "Chantier modifié avec succès", "data", chantier));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_SUPPRIMER')")
    public ResponseEntity<?> supprimerChantier(@PathVariable Long id) {
        chantierService.supprimerChantier(id);
        return ResponseEntity.ok(Map.of("message", "Chantier supprimé avec succès"));
    }

    @GetMapping
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    public ResponseEntity<?> voirListeChantier() {
        return ResponseEntity.ok(chantierService.voirListeChantier());
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR_STATS')")
    public ResponseEntity<Map<String, Object>> voirStats(@PathVariable Long id) {
        Map<String, Object> stats = chantierService.voirStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    public ResponseEntity<Chantier> SelectionnerUnChantier(@PathVariable Long id) {
        return ResponseEntity.ok(chantierService.SelectionnerUnChantier(id));
    }

    @GetMapping("/{id}/urgentes")
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public ResponseEntity<List<Tache>> TachesUrgentesParChantier(@PathVariable Long id) {
        return ResponseEntity.ok(chantierService.TachesUrgentesParChantier(id));
    }

    @GetMapping("/mon-chantier")
    public ResponseEntity<Chantier> monChantier() {
        return ResponseEntity.ok(chantierService.monChantier());
    }
}