package com.example.demo.Security;

import com.example.demo.Entity.Permission;
import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.PermissionRepository;
import com.example.demo.Repository.ProfilRepository;
import com.example.demo.Repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final ProfilRepository profilRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PermissionRepository permissionRepository,
                           ProfilRepository profilRepository,
                           UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.profilRepository = profilRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // ==========================================
        // 1. CRÉATION / RESTAURATION DE TOUTES LES PERMISSIONS
        // ==========================================
        List<String> nomsPermissions = Arrays.asList(
                "CHANTIER_CREER", "CHANTIER_MODIFIER", "CHANTIER_SUPPRIMER", "CHANTIER_VOIR", "CHANTIER_VOIR_STATS",
                "TACHE_CREER", "TACHE_MODIFIER", "TACHE_SUPPRIMER", "TACHE_VOIR", "TACHE_VALIDER",
                "TACHE_ASSIGNER_EQUIPE", "TACHE_ASSIGNER_UTILISATEUR",
                "EQUIPE_CREER", "EQUIPE_MODIFIER", "EQUIPE_SUPPRIMER", "EQUIPE_VOIR", "EQUIPE_GERER_MEMBRES",
                "UTILISATEUR_VOIR", "UTILISATEUR_CREER", "UTILISATEUR_MODIFIER", "UTILISATEUR_SUPPRIMER",
                "GERER_HABILITATIONS"
        );

        for (String nom : nomsPermissions) {
            if (permissionRepository.findByNom(nom).isEmpty()) {
                permissionRepository.save(newPermission(nom));
                System.out.println("✅ Permission restaurée : " + nom);
            }
        }

        // ==========================================
        // 2. CRÉATION DES PROFILS
        // ==========================================
        Profil adminProfil = profilRepository.findByNom("ADMIN").orElseGet(() -> {
            Profil p = new Profil();
            p.setNom("ADMIN");
            p.setDateCreation(LocalDateTime.now());
            p.setDateModification(LocalDateTime.now());
            return profilRepository.save(p);
        });

        Profil userProfil = profilRepository.findByNom("USER").orElseGet(() -> {
            Profil p = new Profil();
            p.setNom("USER");
            p.setDateCreation(LocalDateTime.now());
            p.setDateModification(LocalDateTime.now());
            return profilRepository.save(p);
        });

        // ==========================================
        // 3. ATTRIBUTION DE TOUTES LES PERMISSIONS À L'ADMIN
        // ==========================================
        permissionRepository.findAll().forEach(permission -> {
            if (!adminProfil.getPermissions().contains(permission)) {
                adminProfil.getPermissions().add(permission);
            }
        });
        profilRepository.save(adminProfil);

        // ==========================================
        // 4. ATTRIBUTION DES PERMISSIONS AU PROFIL USER
        // ==========================================
        List.of("CHANTIER_VOIR", "TACHE_VOIR", "TACHE_VALIDER", "EQUIPE_VOIR").forEach(nom -> {
            permissionRepository.findByNom(nom).ifPresent(perm -> {
                if (!userProfil.getPermissions().contains(perm)) {
                    userProfil.getPermissions().add(perm);
                }
            });
        });
        profilRepository.save(userProfil);

        // ==========================================
        // 5. CRÉATION DE L'ADMIN PAR DÉFAUT
        // ==========================================
        if (utilisateurRepository.findByEmail("admin@example.com").isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("Super");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setTelephone("0102030405");
            admin.setProfil(adminProfil);
            admin.setDatecreation(LocalDateTime.now());
            admin.setDateModification(LocalDateTime.now());
            utilisateurRepository.save(admin);
            System.out.println("✅ Admin créé : admin@example.com / admin123");
        }
    }

    private Permission newPermission(String nom) {
        Permission p = new Permission();
        p.setNom(nom);
        return p;
    }
}