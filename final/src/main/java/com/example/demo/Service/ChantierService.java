package com.example.demo.Service;

import com.example.demo.Dto.ChantierDto;
import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Mestypes.Priorite;
import com.example.demo.Entity.Mestypes.StatusChantier;
import com.example.demo.Entity.Mestypes.StatusTache;
import com.example.demo.Entity.Tache;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.ChantierRepository;
import com.example.demo.Repository.TacheRepository;
import com.example.demo.Repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChantierService {
    private final ChantierRepository chantierRepository;
    private final TacheRepository tacheRepository;
    private final UtilisateurRepository utilisateurRepository;

    private Utilisateur Verification(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email).orElseThrow(()->new RuntimeException("L'utilisateur n'existe pas"));
    }
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_CREER')")
    @Transactional
    public Chantier creerChantier(ChantierDto dto){
        if (chantierRepository.findByNom(dto.getNom().trim()).isPresent()) {
            throw new RuntimeException("Un chantier avec ce nom existe déjà");
        }
        Chantier chantier = new Chantier();
        chantier.setNom(dto.getNom().trim());
        chantier.setType(dto.getType());
        chantier.setLocalisation( dto.getLocalisation());
        chantier.setLongitude(dto.getLongitude());
        chantier.setLatitude(dto.getLatitude());
        chantier.setDatedebut(dto.getDatedebut());
        chantier.setDatefin(dto.getDatefin());
        chantier.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusChantier.EN_COURS);
        chantier.setCreateur(Verification());
        return chantierRepository.save(chantier);

    }
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    @Transactional
    public Chantier modifierChantier(Long id, ChantierDto dto){

        Chantier chantier = chantierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Le chantier est introuvable"));

        if (!chantier.getNom().equals(dto.getNom().trim()) && chantierRepository.findByNom(dto.getNom().trim()).isPresent()) {
            throw new RuntimeException("Un chantier avec ce nom existe déjà");
        }
        chantier.setNom(dto.getNom().trim());
        chantier.setDatedebut(dto.getDatedebut());
        chantier.setDatefin(dto.getDatefin());
        chantier.setLocalisation(dto.getLocalisation());
        chantier.setLatitude(dto.getLatitude());
        chantier.setLongitude(dto.getLongitude());
        if (dto.getStatus() != null) chantier.setStatus(dto.getStatus());

        return chantierRepository.save(chantier);
    }
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_SUPPRIMER')")
    @Transactional
    public void supprimerChantier(Long id){
        if(!chantierRepository.existsById(id)){
            throw new RuntimeException("Le chantier introuvable");
        }
        chantierRepository.deleteById(id);
    }

    //LISTE DE TOUT LE CHANTIERS (filtré par utilisateur)
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    @Transactional
    public List<Chantier> voirListeChantier(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = utilisateurRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();

        boolean isAdmin = user.getProfil() != null && "ADMIN".equalsIgnoreCase(user.getProfil().getNom());
        if (isAdmin) {
            return chantierRepository.findAll();
        }

        // Non-admin: chantiers assigns directement OU chantier de son equipe
        List<Chantier> chantierEquipe = List.of();
        if (user.getEquipe() != null && user.getEquipe().getChantier() != null) {
            chantierEquipe = List.of(user.getEquipe().getChantier());
        }

        List<Chantier> chantierAssignes = chantierRepository.findChantiersByUtilisateurId(user.getId());

        // Fusionner sans doublons
        java.util.Set<Long> seenIds = new java.util.HashSet<>();
        java.util.List<Chantier> result = new java.util.ArrayList<>();
        for (Chantier c : chantierEquipe) {
            if (seenIds.add(c.getId())) result.add(c);
        }
        for (Chantier c : chantierAssignes) {
            if (seenIds.add(c.getId())) result.add(c);
        }
        return result;
    }
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR_STATS')")
    public Map<String,Object> voirStats(Long id){
        Chantier chantier = chantierRepository.findById(id).orElseThrow(()->new RuntimeException("Le chantier introuvable"));
        long total=tacheRepository.countByChantierId(id);
        long termines=tacheRepository.countByChantierIdAndStatus(id, StatusTache.TERMINE);
        long enCours=tacheRepository.countByChantierIdAndStatus(id,StatusTache.EN_COURS);
        long aFaire = tacheRepository.countByChantierIdAndStatus(id, StatusTache.A_FAIRE);
        double pourcentage=total==0?0:(double)termines/total*100;

        Map<String, Object> stats = new HashMap<>();
        stats.put("chantier", chantier);
        stats.put("totalTaches", total);
        stats.put("tachesTerminees", termines);
        stats.put("tachesEnCours", enCours);
        stats.put("tachesAFaire", aFaire);
        stats.put("pourcentageAchevement", Math.round(pourcentage * 100.0) / 100.0);
        return stats;
    }
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    public List<Tache> TachesUrgentesParChantier(Long chantierId) {
        return tacheRepository.findByChantierIdAndPriorite(chantierId, Priorite.URGENTE);
    }
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    public Chantier SelectionnerUnChantier(Long id) {
        return chantierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chantier introuvable avec l'ID : " + id));
    }

    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    @Transactional
    public Chantier changerStatus(Long id, StatusChantier nouveauStatus) {
        Chantier chantier = chantierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chantier introuvable"));
        chantier.setStatus(nouveauStatus);
        return chantierRepository.save(chantier);
    }

    // 10. MON CHANTIER (pour le profil USER)
    public Chantier monChantier() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (user.getEquipe() == null || user.getEquipe().getChantier() == null) {
            throw new RuntimeException("Vous n'êtes assigné à aucun chantier");
        }
        return user.getEquipe().getChantier();
    }

    // 11. ASSIGNER UN UTILISATEUR A UN CHANTIER
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    @Transactional
    public void assignerUtilisateur(Long chantierId, Long utilisateurId) {
        Chantier chantier = chantierRepository.findById(chantierId)
                .orElseThrow(() -> new RuntimeException("Chantier introuvable"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        chantier.getUtilisateursAssiges().add(utilisateur);
        chantierRepository.save(chantier);
    }

    // 12. RETIRER UN UTILISATEUR D UN CHANTIER
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    @Transactional
    public void retirerUtilisateur(Long chantierId, Long utilisateurId) {
        Chantier chantier = chantierRepository.findById(chantierId)
                .orElseThrow(() -> new RuntimeException("Chantier introuvable"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        chantier.getUtilisateursAssiges().remove(utilisateur);
        chantierRepository.save(chantier);
    }

    // 13. LISTER LES UTILISATEURS D UN CHANTIER
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    public List<Utilisateur> listerUtilisateursChantier(Long chantierId) {
        Chantier chantier = chantierRepository.findById(chantierId)
                .orElseThrow(() -> new RuntimeException("Chantier introuvable"));
        return new java.util.ArrayList<>(chantier.getUtilisateursAssiges());
    }
}
