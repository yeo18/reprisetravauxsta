package com.example.demo.Service;

import com.example.demo.Dto.EquipeDto;
import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Equipe;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.ChantierRepository;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final ChantierRepository chantierRepository;
    private final UtilisateurRepository utilisateurRepository;

    // 1. CREER
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_CREER')")
    @Transactional
    public Equipe creerEquipe(EquipeDto dto) {
        if (equipeRepository.findByNom(dto.getNom().trim()).isPresent()) {
            throw new RuntimeException("Une équipe avec ce nom existe déjà");
        }
        Chantier chantier = chantierRepository.findById(dto.getChantierId())
                .orElseThrow(() -> new RuntimeException("Chantier introuvable"));
        Equipe equipe = new Equipe();
        equipe.setNom(dto.getNom().trim());
        equipe.setDomaine(dto.getDomaine());
        equipe.setChantier(chantier);
        return equipeRepository.save(equipe);
    }

    // 2. MODIFIER
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_MODIFIER')")
    @Transactional
    public Equipe modifierEquipe(Long id, EquipeDto dto) {
        Equipe equipe = EquipeUnique(id);
        if (!equipe.getChantier().getId().equals(dto.getChantierId())) {
            Chantier chantier = chantierRepository.findById(dto.getChantierId())
                    .orElseThrow(() -> new RuntimeException("Chantier introuvable"));
            equipe.setChantier(chantier);
        }
        if (!equipe.getNom().equals(dto.getNom().trim()) && equipeRepository.findByNom(dto.getNom().trim()).isPresent()) {
            throw new RuntimeException("Une équipe avec ce nom existe déjà");
        }
        equipe.setNom(dto.getNom().trim());
        if (dto.getDomaine() != null) {
            equipe.setDomaine(dto.getDomaine());
        }
        return equipeRepository.save(equipe);
    }

    // 3. SUPPRIMER
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_SUPPRIMER')")
    @Transactional
    public void supprimerEquipe(Long id) {
        if (!equipeRepository.existsById(id)) {
            throw new RuntimeException("Équipe introuvable");
        }
        equipeRepository.deleteById(id);
    }

    // 4. LISTER TOUTES (filtré par chantiers de l'utilisateur)
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_VOIR')")
    public List<Equipe> listerToutes() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = utilisateurRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();

        boolean isAdmin = user.getProfil() != null && "ADMIN".equalsIgnoreCase(user.getProfil().getNom());
        if (isAdmin) {
            return equipeRepository.findAll();
        }

        // Non-admin: equipes des chantiers assigns + equipe de son chantier
        java.util.Set<Long> chantierIds = new java.util.HashSet<>();
        List<Chantier> chantierAssignes = chantierRepository.findChantiersByUtilisateurId(user.getId());
        for (Chantier c : chantierAssignes) {
            chantierIds.add(c.getId());
        }
        if (user.getEquipe() != null && user.getEquipe().getChantier() != null) {
            chantierIds.add(user.getEquipe().getChantier().getId());
        }

        if (chantierIds.isEmpty()) {
            return List.of();
        }
        return equipeRepository.findByChantierIdIn(new java.util.ArrayList<>(chantierIds));
    }

    // 5. VOIR UNE
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_VOIR')")
    public Equipe EquipeUnique(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipe introuvable"));
    }

    // 6. AJOUTER UN MEMBRE (Un utilisateur ne peut appartenir qu'à une seule équipe)
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_GERER_MEMBRES')")
    @Transactional
    public void ajouterMembre(Long equipeId, Long utilisateurId) {
        Equipe equipe = EquipeUnique(equipeId);
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        // Si l'utilisateur est déjà dans une autre équipe, on le change
        utilisateur.setEquipe(equipe);
        utilisateurRepository.save(utilisateur);
    }

    // 7. RETIRER UN MEMBRE
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_GERER_MEMBRES')")
    @Transactional
    public void retirerMembre(Long equipeId, Long utilisateurId) {
        Equipe equipe = EquipeUnique(equipeId);
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (utilisateur.getEquipe() != null && utilisateur.getEquipe().getId().equals(equipeId)) {
            utilisateur.setEquipe(null);
            utilisateurRepository.save(utilisateur);
        } else {
            throw new RuntimeException("Cet utilisateur n'appartient pas à cette équipe");
        }
    }

    // 8. CALCULER NOMBRE DE MEMBRES
    @PreAuthorize("@securityEvaluator.hasPermission('EQUIPE_VOIR')")
    public int calculerNombreMembres(Long equipeId) {
        Equipe equipe = EquipeUnique(equipeId);
        return equipe.getMembres().size();
    }

    // 9. MON EQUIPE (pour le profil USER)
    public Equipe monEquipe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (user.getEquipe() == null) {
            throw new RuntimeException("Vous n'êtes assigné à aucune équipe");
        }
        return user.getEquipe();
    }
}