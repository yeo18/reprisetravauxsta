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
        // 1. CRÉATION DE TOUTES LES PERMISSIONS
        // ==========================================
        if (permissionRepository.count() == 0) {
            List<Permission> permissions = Arrays.asList(
                    // Chantier
                    newPermission("CHANTIER_CREER"),
                    newPermission("CHANTIER_MODIFIER"),
                    newPermission("CHANTIER_SUPPRIMER"),
                    newPermission("CHANTIER_VOIR"),
                    newPermission("CHANTIER_VOIR_STATS"),      // ✅ AJOUTÉE

                    // Tache
                    newPermission("TACHE_CREER"),
                    newPermission("TACHE_MODIFIER"),
                    newPermission("TACHE_SUPPRIMER"),
                    newPermission("TACHE_VOIR"),
                    newPermission("TACHE_ASSIGNER_EQUIPE"),    // ✅ AJOUTÉE
                    newPermission("TACHE_ASSIGNER_UTILISATEUR"), // ✅ AJOUTÉE

                    // Equipe
                    newPermission("EQUIPE_CREER"),             // ✅ AJOUTÉE
                    newPermission("EQUIPE_MODIFIER"),          // ✅ AJOUTÉE
                    newPermission("EQUIPE_SUPPRIMER"),         // ✅ AJOUTÉE
                    newPermission("EQUIPE_VOIR"),              // ✅ AJOUTÉE
                    newPermission("EQUIPE_GERER_MEMBRES"),     // ✅ AJOUTÉE

                    // Admin
                    newPermission("GERER_HABILITATIONS")
            );
            permissionRepository.saveAll(permissions);
            System.out.println("✅ Toutes les permissions créées avec succès");
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
        // 4. CRÉATION DE L'ADMIN PAR DÉFAUT
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
            System.out.println(" Admin créé : admin@example.com / admin123");
        }
    }

    // Méthode utilitaire pour créer une permission
    private Permission newPermission(String nom) {
        Permission p = new Permission();
        p.setNom(nom);
        return p;
    }
}