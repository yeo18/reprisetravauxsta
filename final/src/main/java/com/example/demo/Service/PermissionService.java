package com.example.demo.Service;

import com.example.demo.Entity.Permission;
import com.example.demo.Repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;

    // ========== LECTURE ==========

    public List<Permission> listerToutes() {
        return permissionRepository.findAll();
    }

    public Permission trouverParId(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée avec l'ID : " + id));
    }

    public Permission trouverParNom(String nom) {
        return permissionRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée avec le nom : " + nom));
    }

    public boolean existeParNom(String nom) {
        return permissionRepository.findByNom(nom).isPresent();
    }

    // ========== ÉCRITURE ==========

    @Transactional
    public Permission creerPermission(String nom, String description) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new RuntimeException("Le nom de la permission ne peut pas être vide");
        }
        if (existeParNom(nom.trim())) {
            throw new RuntimeException("Une permission avec le nom '" + nom + "' existe déjà");
        }
        Permission permission = new Permission();
        permission.setNom(nom.trim());
        permission.setDescription(description);
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission modifierPermission(Long id, String nouveauNom) {
        if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
            throw new RuntimeException("Le nouveau nom ne peut pas être vide");
        }
        Permission permission = trouverParId(id);
        if (!permission.getNom().equals(nouveauNom.trim())) {
            if (existeParNom(nouveauNom.trim())) {
                throw new RuntimeException("Une permission avec le nom '" + nouveauNom + "' existe déjà");
            }
            permission.setNom(nouveauNom.trim());
        }
        return permissionRepository.save(permission);
    }

    @Transactional
    public void supprimerPermission(Long id) {
        Permission permission = trouverParId(id);
        permissionRepository.delete(permission);
    }
}