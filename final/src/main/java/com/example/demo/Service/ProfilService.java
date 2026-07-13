package com.example.demo.Service;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Profil;
import com.example.demo.Repository.PermissionRepository;
import com.example.demo.Repository.ProfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilService {

    private final ProfilRepository profilRepository;
    private final PermissionRepository permissionRepository;

    public List<Profil> listerTous() {
        return profilRepository.findAll();
    }

    public Profil trouverParId(Long id) {
        return profilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
    }

    public Profil trouverParNom(String nom) {
        return profilRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));
    }

    @Transactional
    public Profil creerProfil(String nom, String description) {
        // Vérifier l'unicité du nom
        if (profilRepository.findByNom(nom).isPresent()) {
            throw new RuntimeException("Un profil avec ce nom existe déjà");
        }
        Profil profil = new Profil();
        profil.setNom(nom);
        profil.setDateCreation(LocalDateTime.now());
        profil.setDateModification(LocalDateTime.now());
        return profilRepository.save(profil);
    }

    @Transactional
    public Profil modifierProfil(Long id, String nouveauNom) {
        Profil profil = trouverParId(id);
        if (!profil.getNom().equals(nouveauNom)) {
            if (profilRepository.findByNom(nouveauNom).isPresent()) {
                throw new RuntimeException("Un profil avec ce nom existe déjà");
            }
            profil.setNom(nouveauNom);
        }
        profil.setDateModification(LocalDateTime.now());
        return profilRepository.save(profil);
    }

    @Transactional
    public void supprimerProfil(Long id) {
        Profil profil = trouverParId(id);
        // Optionnel : vérifier qu'aucun utilisateur n'est associé à ce profil
        // Si des utilisateurs existent, on peut lever une exception
        profilRepository.delete(profil);
    }

    @Transactional
    public void ajouterPermissionAuProfil(Long profilId, Long permissionId) {
        Profil profil = trouverParId(profilId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée"));
        profil.getPermissions().add(permission);
        profil.setDateModification(LocalDateTime.now());
        profilRepository.save(profil);
    }

    @Transactional
    public void retirerPermissionDuProfil(Long profilId, Long permissionId) {
        Profil profil = trouverParId(profilId);
        boolean removed = profil.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        if (!removed) {
            throw new RuntimeException("Cette permission n'est pas associée à ce profil");
        }
        profil.setDateModification(LocalDateTime.now());
        profilRepository.save(profil);
    }
}