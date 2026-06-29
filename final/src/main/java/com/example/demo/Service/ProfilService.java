package com.example.demo.Service;

import com.example.demo.Entity.Profil;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.ProfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilService {
    private final ProfilRepository profilRepository;
    public Profil trouverParNom(String nom) {
        return profilRepository.findByNom(nom).orElseThrow(()->new RuntimeException("Profil non trouvé"+nom));
    }

}
