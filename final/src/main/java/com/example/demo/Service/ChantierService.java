package com.example.demo.Service;

import com.example.demo.Dto.ChantierDto;
import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Mestypes.Priorite;
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
        Chantier chantier = new Chantier();
        chantier.setNom(dto.getNom());
        chantier.setType(dto.getType());
        chantier.setLocalisation( dto.getLocalisation());
        chantier.setLongitude(dto.getLongitude());
        chantier.setLatitude(dto.getLatitude());
        chantier.setDatedebut(dto.getDatedebut());
        chantier.setDatefin(dto.getDatefin());
        chantier.setCreateur(Verification());
        return chantierRepository.save(chantier);

    }
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_MODIFIER')")
    @Transactional
    public Chantier modifierChantier(Long id, ChantierDto dto){

        Chantier chantier = chantierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Le chantier est introuvable"));

        chantier.setNom(dto.getNom());
        chantier.setDatedebut(dto.getDatedebut());
        chantier.setDatefin(dto.getDatefin());
        chantier.setLocalisation(dto.getLocalisation());
        chantier.setLatitude(dto.getLatitude());
        chantier.setLongitude(dto.getLongitude());

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

    //LISTE DE TOUT LE CHANTIERS
    @PreAuthorize("@securityEvaluator.hasPermission('CHANTIER_VOIR')")
    @Transactional
    public List<Chantier>   voirListeChantier(){
        return chantierRepository.findAll();
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
}
