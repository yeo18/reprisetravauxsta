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
import com.example.demo.Security.SecurityEvaluator;
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

    // 4. LISTER TOUTES (filtré par role/utilisateur)
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public List<Tache> getAllTaches() {
        Utilisateur user = getCurrentUser();
        boolean isAdmin = user.getProfil() != null && "ADMIN".equalsIgnoreCase(user.getProfil().getNom());
        if (isAdmin) {
            return tacheRepository.findAll();
        }

        // Verifier si l'utilisateur a la permission TACHE_VALIDER
        boolean hasValider = hasPermission(user, "TACHE_VALIDER");
        if (hasValider) {
            // Voir toutes les tâches des chantiers assigns
            List<Long> chantierIds = chantierRepository.findChantiersByUtilisateurId(user.getId()).stream()
                    .map(Chantier::getId).toList();
            if (chantierIds.isEmpty()) {
                return List.of();
            }
            return tacheRepository.findByChantierIdIn(chantierIds);
        }

        // Utilisateur normal: ses tâches + tâches de son équipe
        return mesTaches();
    }

    // 5. MES TACHES (pour un utilisateur USER — filtré par assignation + équipe + chantiers assigns)
    public List<Tache> mesTaches() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Si l'utilisateur a TACHE_VALIDER, il voit toutes les tâches de ses chantiers assigns
        boolean hasValider = hasPermission(user, "TACHE_VALIDER");
        if (hasValider) {
            List<Long> chantierIds = chantierRepository.findChantiersByUtilisateurId(user.getId()).stream()
                    .map(Chantier::getId).toList();
            if (!chantierIds.isEmpty()) {
                return tacheRepository.findByChantierIdIn(chantierIds);
            }
        }

        // Sinon: tâches assignées directement + tâches de son équipe
        Equipe userEquipe = user.getEquipe();
        if (userEquipe != null) {
            return tacheRepository.findByAssigneAIdOrEquipesIdIn(
                    user.getId(),
                    List.of(userEquipe.getId())
            );
        }
        return tacheRepository.findByAssigneAId(user.getId());
    }

    // 6. VOIR UNE
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public Tache TacheParId(Long id) {
        return tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche introuvable"));
    }

    // 6. LISTER PAR CHANTIER (avec verification d'acces)
    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public List<Tache> TacheParChantier(Long chantierId) {
        Utilisateur user = getCurrentUser();
        boolean isAdmin = user.getProfil() != null && "ADMIN".equalsIgnoreCase(user.getProfil().getNom());
        if (!isAdmin) {
            // Verifier que l'utilisateur a acces a ce chantier
            boolean isAssigned = chantierRepository.isUtilisateurAssignedToChantier(chantierId, user.getId());
            boolean isTeamChantier = user.getEquipe() != null
                    && user.getEquipe().getChantier() != null
                    && user.getEquipe().getChantier().getId().equals(chantierId);
            if (!isAssigned && !isTeamChantier) {
                throw new RuntimeException("Vous n'avez pas acces a ce chantier");
            }
        }
        return tacheRepository.findByChantierId(chantierId);
    }

    // 6b. MES TACHES PAR CHANTIER (pour un utilisateur USER)
    public List<Tache> mesTachesParChantier(Long chantierId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Si l'utilisateur a TACHE_VALIDER, il voit toutes les tâches de ce chantier
        boolean hasValider = hasPermission(user, "TACHE_VALIDER");
        if (hasValider) {
            boolean isAssigned = chantierRepository.isUtilisateurAssignedToChantier(chantierId, user.getId());
            boolean isTeamChantier = user.getEquipe() != null
                    && user.getEquipe().getChantier() != null
                    && user.getEquipe().getChantier().getId().equals(chantierId);
            if (isAssigned || isTeamChantier) {
                return tacheRepository.findByChantierId(chantierId);
            }
            return List.of();
        }

        // Sinon: tâches assignées directement + tâches de son équipe pour ce chantier
        Equipe userEquipe = user.getEquipe();
        if (userEquipe != null) {
            return tacheRepository.findByChantierIdAndAssigneAIdOrEquipesIdIn(
                    chantierId, user.getId(), List.of(userEquipe.getId())
            );
        }
        return tacheRepository.findByChantierId(chantierId).stream()
                .filter(t -> t.getAssigneA() != null && t.getAssigneA().getId().equals(user.getId()))
                .toList();
    }

    private boolean hasPermission(Utilisateur user, String permission) {
        // Admin a tous les droits
        if (user.getProfil() != null && "ADMIN".equalsIgnoreCase(user.getProfil().getNom())) {
            return true;
        }
        // Verifier dans les permissions du profil
        boolean viaProfil = user.getProfil() != null &&
                user.getProfil().getPermissions().stream()
                        .anyMatch(p -> p.getNom().equalsIgnoreCase(permission));
        // Verifier dans les permissions specifiques
        boolean viaSpecifique = user.getPermissionsSpecifiques().stream()
                .anyMatch(p -> p.getNom().equalsIgnoreCase(permission));
        return viaProfil || viaSpecifique;
    }

    // ========== STATS ==========

    private Utilisateur getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    private List<Long> getCurrentUserEquipeIds(Utilisateur user) {
        if (user.getEquipe() != null) {
            return List.of(user.getEquipe().getId());
        }
        return List.of();
    }

    public com.example.demo.Dto.MesStatsDto mesStats() {
        Utilisateur user = getCurrentUser();
        List<Long> equipeIds = getCurrentUserEquipeIds(user);
        List<Tache> toutes = equipeIds.isEmpty()
                ? tacheRepository.findByAssigneAId(user.getId())
                : tacheRepository.findByAssigneAIdOrEquipesIdIn(user.getId(), equipeIds);

        return buildStats(toutes, user, equipeIds);
    }

    public com.example.demo.Dto.MesStatsDto mesStatsParDates(java.time.LocalDate start, java.time.LocalDate end) {
        Utilisateur user = getCurrentUser();
        List<Long> equipeIds = getCurrentUserEquipeIds(user);
        List<Tache> toutes = equipeIds.isEmpty()
                ? tacheRepository.findByAssigneAId(user.getId()).stream()
                    .filter(t -> t.getDatedebut() != null && !t.getDatedebut().isBefore(start) && !t.getDatedebut().isAfter(end))
                    .toList()
                : tacheRepository.findByAssigneAIdOrEquipesIdInAndDatedebutBetween(user.getId(), equipeIds, start, end);
        if (toutes.isEmpty()) {
            toutes = equipeIds.isEmpty()
                    ? tacheRepository.findByAssigneAId(user.getId()).stream()
                        .filter(t -> t.getDatefin() != null && !t.getDatefin().isBefore(start) && !t.getDatefin().isAfter(end))
                        .toList()
                    : tacheRepository.findByAssigneAIdOrEquipesIdInAndDatefinBetween(user.getId(), equipeIds, start, end);
        }
        return buildStats(toutes, user, equipeIds);
    }

    public com.example.demo.Dto.MesStatsDto mesStatsParChantier(Long chantierId) {
        Utilisateur user = getCurrentUser();
        List<Long> equipeIds = getCurrentUserEquipeIds(user);
        List<Tache> toutes = equipeIds.isEmpty()
                ? tacheRepository.findByChantierId(chantierId).stream()
                    .filter(t -> t.getAssigneA() != null && t.getAssigneA().getId().equals(user.getId()))
                    .toList()
                : tacheRepository.findByChantierIdAndAssigneAIdOrEquipesIdIn(chantierId, user.getId(), equipeIds);

        com.example.demo.Dto.MesStatsDto dto = buildStats(toutes, user, equipeIds);
        java.util.Optional<com.example.demo.Dto.MesStatsDto.StatsParChantier> chantierStat = dto.getParChantier().stream()
                .filter(s -> s.getChantierId().equals(chantierId))
                .findFirst();
        if (chantierStat.isPresent()) {
            dto.setParChantier(java.util.List.of(chantierStat.get()));
        }
        return dto;
    }

    public com.example.demo.Dto.MesStatsDto mesStatsParChantierAvecDates(Long chantierId, java.time.LocalDate start, java.time.LocalDate end) {
        Utilisateur user = getCurrentUser();
        List<Long> equipeIds = getCurrentUserEquipeIds(user);

        java.util.function.Predicate<Tache> inRange = t ->
                (t.getDatedebut() != null && !t.getDatedebut().isBefore(start) && !t.getDatedebut().isAfter(end))
                || (t.getDatefin() != null && !t.getDatefin().isBefore(start) && !t.getDatefin().isAfter(end));

        List<Tache> toutes;
        if (equipeIds.isEmpty()) {
            toutes = tacheRepository.findByChantierId(chantierId).stream()
                    .filter(t -> t.getAssigneA() != null && t.getAssigneA().getId().equals(user.getId()))
                    .filter(inRange)
                    .toList();
        } else {
            List<Tache> sansDate = tacheRepository.findByChantierIdAndAssigneAIdOrEquipesIdIn(chantierId, user.getId(), equipeIds);
            toutes = sansDate.stream().filter(inRange).toList();
        }

        com.example.demo.Dto.MesStatsDto dto = buildStats(toutes, user, equipeIds);
        java.util.Optional<com.example.demo.Dto.MesStatsDto.StatsParChantier> chantierStat = dto.getParChantier().stream()
                .filter(s -> s.getChantierId().equals(chantierId))
                .findFirst();
        if (chantierStat.isPresent()) {
            dto.setParChantier(java.util.List.of(chantierStat.get()));
        }
        return dto;
    }

    private com.example.demo.Dto.MesStatsDto buildStats(List<Tache> toutes, Utilisateur user, List<Long> equipeIds) {
        com.example.demo.Dto.MesStatsDto dto = new com.example.demo.Dto.MesStatsDto();
        dto.setTotalTaches(toutes.size());
        dto.setTachesTerminees(toutes.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.TERMINE).count());
        dto.setTachesEnCours(toutes.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.EN_COURS).count());
        dto.setTachesAFaire(toutes.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.A_FAIRE).count());
        dto.setTachesBloquees(toutes.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.EN_ATTENTE || t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.SUSPENDU).count());
        dto.setTachesIndividuelles(toutes.stream().filter(t -> t.getAssigneA() != null && t.getAssigneA().getId().equals(user.getId())).count());
        dto.setTachesEquipe(toutes.stream().filter(t -> t.getAssigneA() == null || !t.getAssigneA().getId().equals(user.getId())).count());
        dto.setTauxAchevement(toutes.isEmpty() ? 0 : Math.round((double) dto.getTachesTerminees() / toutes.size() * 10000.0) / 100.0);

        java.util.Map<Long, java.util.List<Tache>> parChantier = toutes.stream()
                .collect(java.util.stream.Collectors.groupingBy(t -> t.getChantier().getId()));

        java.util.List<com.example.demo.Dto.MesStatsDto.StatsParChantier> chantiers = new java.util.ArrayList<>();
        for (java.util.Map.Entry<Long, java.util.List<Tache>> entry : parChantier.entrySet()) {
            java.util.List<Tache> tasks = entry.getValue();
            com.example.demo.Dto.MesStatsDto.StatsParChantier sc = new com.example.demo.Dto.MesStatsDto.StatsParChantier();
            sc.setChantierId(entry.getKey());
            sc.setChantierNom(tasks.get(0).getChantier().getNom());
            sc.setTotal(tasks.size());
            sc.setIndividuelles(tasks.stream().filter(t -> t.getAssigneA() != null && t.getAssigneA().getId().equals(user.getId())).count());
            sc.setEquipe(tasks.stream().filter(t -> t.getAssigneA() == null || !t.getAssigneA().getId().equals(user.getId())).count());
            sc.setTerminees(tasks.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.TERMINE).count());
            sc.setEnCours(tasks.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.EN_COURS).count());
            sc.setAFaire(tasks.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.A_FAIRE).count());
            sc.setBloquees(tasks.stream().filter(t -> t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.EN_ATTENTE || t.getStatus() == com.example.demo.Entity.Mestypes.StatusTache.SUSPENDU).count());
            sc.setTauxAchevement(tasks.isEmpty() ? 0 : Math.round((double) sc.getTerminees() / tasks.size() * 10000.0) / 100.0);
            chantiers.add(sc);
        }
        dto.setParChantier(chantiers);
        return dto;
    }

    // ========== TOTAL SYSTÈME ==========

    @PreAuthorize("@securityEvaluator.hasPermission('TACHE_VOIR')")
    public long totalTaches() {
        return tacheRepository.count();
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