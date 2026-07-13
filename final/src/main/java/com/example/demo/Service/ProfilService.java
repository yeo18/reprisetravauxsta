package com.example.demo.Service;

import com.example.demo.Entity.Profil;
import com.example.demo.Repository.ProfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilService {

    private final ProfilRepository profilRepository;


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
}