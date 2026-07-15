package com.example.demo.Service;

import com.example.demo.Dto.TacheDto;
import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Equipe;
import com.example.demo.Entity.Tache;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.ChantierRepository;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.TacheRepository;
import com.example.demo.Repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TacheService {

    private final TacheRepository tacheRepository;
    private final ChantierRepository chantierRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EquipeRepository equipeRepository;

    // 1. CREER
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_CREER')")
    @Transactional
    public Tache creerTache(TacheDto dto) {
        // Récupérer le chantier
        Chantier chantier = chantierRepository.findById(dto.getChantierId())
                .orElseThrow(() -> new RuntimeException("Chantier introuvable"));

        // Récupérer l'utilisateur (si fourni)
        Utilisateur assigneA = null;
        if (dto.getUtilisateurId() != null) {
            assigneA = utilisateurRepository.findById(dto.getUtilisateurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        }

        // Récupérer l'équipe (si fournie)
        Equipe equipe = null;
        if (dto.getEquipeId() != null) {
            equipe = equipeRepository.findById(dto.getEquipeId())
                    .orElseThrow(() -> new RuntimeException("Équipe introuvable"));
        }

        Tache tache = new Tache();
        tache.setTitre(dto.getTitre());
        tache.setDescription(dto.getDescription());
        tache.setCoutEstime(dto.getCoutEstime());
        tache.setDureeEstimeeAnnees(dto.getDureeEstimeeAnnees());
        tache.setDureeEstimeeMois(dto.getDureeEstimeeMois());
        tache.setDureeEstimeeJours(dto.getDureeEstimeeJours());
        tache.setPriorite(dto.getPriorite());
        tache.setStatus(dto.getStatus());
        tache.setDatedebut(dto.getDatedebut());
        tache.setDatefin(dto.getDatefin());
        tache.setChantier(chantier);
        tache.setAssigneA(assigneA);
        if (equipe != null) {
            tache.getEquipes().add(equipe);
        }
        return tacheRepository.save(tache);
    }

    // 2. MODIFIER
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_MODIFIER')")
    @Transactional
    public Tache modifierTache(Long id, TacheDto dto) {
        Tache tache = TacheParId(id);

        // Mise à jour du chantier
        if (!tache.getChantier().getId().equals(dto.getChantierId())) {
            Chantier chantier = chantierRepository.findById(dto.getChantierId())
                    .orElseThrow(() -> new RuntimeException("Chantier introuvable"));
            tache.setChantier(chantier);
        }

        // Mise à jour de l'utilisateur assigné
        if (dto.getUtilisateurId() == null) {
            tache.setAssigneA(null);
        } else if (tache.getAssigneA() == null || !tache.getAssigneA().getId().equals(dto.getUtilisateurId())) {
            Utilisateur user = utilisateurRepository.findById(dto.getUtilisateurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
            tache.setAssigneA(user);
        }

        // Mise à jour des équipes (on remplace la liste ici)
        if (dto.getEquipeId() != null) {
            Equipe equipe = equipeRepository.findById(dto.getEquipeId())
                    .orElseThrow(() -> new RuntimeException("Équipe introuvable"));
            tache.getEquipes().clear();
            tache.getEquipes().add(equipe);
        } else {
            tache.getEquipes().clear();
        }

        tache.setTitre(dto.getTitre());
        tache.setDescription(dto.getDescription());
        tache.setCoutEstime(dto.getCoutEstime());
        tache.setDureeEstimeeAnnees(dto.getDureeEstimeeAnnees());
        tache.setDureeEstimeeMois(dto.getDureeEstimeeMois());
        tache.setDureeEstimeeJours(dto.getDureeEstimeeJours());
        tache.setPriorite(dto.getPriorite());
        tache.setStatus(dto.getStatus());
        tache.setDatedebut(dto.getDatedebut());
        tache.setDatefin(dto.getDatefin());

        return tacheRepository.save(tache);
    }

    // 3. SUPPRIMER
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_SUPPRIMER')")
    @Transactional
    public void supprimerTache(Long id) {
        if (!tacheRepository.existsById(id)) {
            throw new RuntimeException("Tâche introuvable");
        }
        tacheRepository.deleteById(id);
    }

    // 4. LISTER TOUTES
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public List<Tache> getAllTaches() {
        return tacheRepository.findAll();
    }

    // 5. MES TACHES (pour un utilisateur USER — filtré par assignation)
    public List<Tache> mesTaches() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return tacheRepository.findByAssigneAId(user.getId());
    }

    // 6. VOIR UNE
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public Tache TacheParId(Long id) {
        return tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche introuvable"));
    }

    // 6. LISTER PAR CHANTIER
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public List<Tache> TacheParChantier(Long chantierId) {
        return tacheRepository.findByChantierId(chantierId);
    }

    // 7. ASSIGNER A UNE EQUIPE (Méthode dédiée)
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_ASSIGNER_EQUIPE')")
    @Transactional
    public void assignerTacheAEquipe(Long tacheId, Long equipeId) {
        Tache tache = TacheParId(tacheId);
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Équipe introuvable"));
        tache.getEquipes().add(equipe);
        tacheRepository.save(tache);
    }

    // 8. ASSIGNER A UN UTILISATEUR (Méthode dédiée)
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_ASSIGNER_UTILISATEUR')")
    @Transactional
    public void assignerTacheAUtilisateur(Long tacheId, Long utilisateurId) {
        Tache tache = TacheParId(tacheId);
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        tache.setAssigneA(user);
        tacheRepository.save(tache);
    }

    // 9. TERMINER UNE TACHE (validation)
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VALIDER')")
    @Transactional
    public void terminerTache(Long id) {
        Tache tache = TacheParId(id);
        tache.setStatus(com.example.demo.Entity.Mestypes.StatusTache.TERMINE);
        tacheRepository.save(tache);
    }
}