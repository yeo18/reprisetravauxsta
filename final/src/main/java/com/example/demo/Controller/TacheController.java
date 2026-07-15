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
    public ResponseEntity<?> creerTache(@RequestBody TacheDto dto) {
        Tache tache=tacheService.creerTache(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tâche créée avec succès", "data", tache));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTache(@PathVariable Long id, @RequestBody TacheDto dto) {
        Tache tache=tacheService.modifierTache(id, dto);
        return ResponseEntity.ok(Map.of("message", "Tâche modifiée avec succès", "data", tache));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerTache(@PathVariable Long id) {
        tacheService.supprimerTache(id);
        return ResponseEntity.ok(Map.of("message", "Tâche supprimée avec succès"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Tache> TacheParId(@PathVariable Long id) {
        return ResponseEntity.ok(tacheService.TacheParId(id));
    }
    //tache par chantier
    @GetMapping("/chantier/{chantierId}")
    public ResponseEntity<List<Tache>> TacheParChantier(@PathVariable Long chantierId) {
        return ResponseEntity.ok(tacheService.TacheParChantier(chantierId));
    }
    @PostMapping("/{tacheId}/aasigner-equipe/{equipeId}")
    public ResponseEntity<?>assignerTacheEquipe( @PathVariable Long tacheId,@PathVariable Long equipeId) {
        tacheService.assignerTacheAEquipe(tacheId,equipeId);
        return ResponseEntity.ok(Map.of("message", "Tâche assignée à l'équipe avec succès"));    }

    @PostMapping("{tacheId}/assigner-utilisateur/{utilisateurId}")
    public ResponseEntity<?>assignerTacheAUtilisateur(@PathVariable Long tacheId,@PathVariable Long utilisateurId) {
        tacheService.assignerTacheAUtilisateur(tacheId,utilisateurId);
        return ResponseEntity.ok(Map.of("message", "Tâche assignée à l'utilisateur avec succès"));

    }

    @GetMapping
    public ResponseEntity<List<Tache>> voirToutesLesTaches() {
        return ResponseEntity.ok(tacheService.getAllTaches());
    }

}

