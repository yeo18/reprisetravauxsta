package com.example.demo.Service;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.PermissionRepository;
import com.example.demo.Repository.ProfilRepository;
import com.example.demo.Repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
@PreAuthorize("@securityEvaluator.hasPermission('GERER_HABILITATIONS')")
public class AdminAcessService {
    private final ProfilRepository profilRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void attribuerDroitProfil(Long profilId,Long PermissionId){
        Profil profil =profilRepository.findById(profilId).orElseThrow(()->new RuntimeException("profil introuvable"));
        Permission permission=permissionRepository.findById(PermissionId).orElseThrow(()->new RuntimeException("permission introuvable"));
        profil.getPermissions().add(permission);
        profilRepository.save(profil);
    }

    @Transactional
    public Profil modifierProfil(Long profilId,String nouveaunom){
        Profil profil=profilRepository.findById(profilId).orElseThrow(()->new RuntimeException("profil introuvable"));
        if(!profil.getNom().equals(nouveaunom)){
            profilRepository.findByNom(nouveaunom).ifPresent(p->{throw new RuntimeException("profil avec ce nom existe deja ");});
        }
        profil.setNom(nouveaunom);
         return profilRepository.save(profil);
    }

    @Transactional
    public void retirerDroitProfil(Long profilId,Long PermissionId){
        Profil profil=profilRepository.findById(profilId).orElseThrow(()->new RuntimeException("profil introuvable"));
        profil.getPermissions().removeIf(p->p.getId().equals(PermissionId));
        profilRepository.save(profil);
    }
    @Transactional
    public  void accorderPermissionSupplementaire(Long utilisateurId,Long PermissionId){
        Utilisateur utilisateur=utilisateurRepository.findById(utilisateurId).orElseThrow(()->new RuntimeException("utilisateur introuvable"));
        Permission permission=permissionRepository.findById(PermissionId).orElseThrow(()->new RuntimeException("permission introuvable"));
        utilisateur.getPermissionsSpecifique().add(permission);
        utilisateurRepository.save(utilisateur);
    }
    @Transactional
    public  void modifierPermission(Long permissionId,String nouveaunom){
        Permission permission=permissionRepository.findById(permissionId).orElseThrow(()->new RuntimeException("Permission introuvable"));
        if(!permission.getNom().equals(nouveaunom)){
            permissionRepository.findByNom(nouveaunom).ifPresent(p->{throw new RuntimeException("une permission avec ce nom existe deja");});
            permission.setNom(nouveaunom);
            permissionRepository.save(permission);
        }
    }
    @Transactional
    public void retirerPermissionSupplementaire(Long utilisateurId,Long PermissionId){
        Utilisateur utilisateur=utilisateurRepository.findById(utilisateurId).orElseThrow(()->new RuntimeException("utilisateur introuvable"));
        utilisateur.getPermissionsSpecifique().removeIf(p->p.getId().equals(PermissionId));
        utilisateurRepository.save(utilisateur);
    }
}
