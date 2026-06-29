package com.example.demo.Controller;

import com.example.demo.Dto.Connexion;
import com.example.demo.Dto.Inscription;
import com.example.demo.Security.JwtUtil;
import com.example.demo.Service.AuthService;
import com.example.demo.Service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Dto.Connexion.ReponseAuthentification;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UtilisateurService utilisateurService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    @PostMapping("/register")
    public ResponseEntity<?>register(@Valid @RequestBody Inscription inscription){
        try{
            utilisateurService.inscrire(inscription);
            return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur cree avec succès");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")

    public ResponseEntity<?>login(@Valid @RequestBody Connexion connexion){
        try{
            authService.authentifier(connexion.getEmail(),connexion.getPassword());
            String token = jwtUtil.generateToken(connexion.getEmail());
            ReponseAuthentification reponse=  connexion.new ReponseAuthentification();
            reponse.setToken(token);
            System.out.println("Tentative de login pour : " + connexion.getEmail());
            return ResponseEntity.ok(reponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
        }
    }
}
