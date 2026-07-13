package com.example.demo.Service;

import com.example.demo.Entity.Permission;
import com.example.demo.Repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<Permission> listerToutes() {
        return permissionRepository.findAll();
    }

    public Permission trouverParId(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission non trouvée"));
    }

    @Transactional
    public Permission modifierPermission(Long id, String nouveauNom) {
        Permission permission = trouverParId(id);
        if (!permission.getNom().equals(nouveauNom)) {
            if (permissionRepository.findByNom(nouveauNom).isPresent()) {
                throw new RuntimeException("Une permission avec ce nom existe déjà");
            }
            permission.setNom(nouveauNom);
        }
        return permissionRepository.save(permission);
    }
}