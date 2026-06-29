package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity

@Getter
@Setter
public class Equipe {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(nullable = false)
    private String nom;

    private String domaine;
    private LocalDateTime creation;
    private LocalDateTime modification;
    @ManyToOne
    @JoinColumn(name = "chantier_id", nullable = false)
    private Chantier chantier;
    @JsonIgnore
    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL)
    private Set<Utilisateur> membres = new HashSet<>();
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "tache_equipe",
            joinColumns = @JoinColumn(name = "equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tache_id")
    )
    private Set<Tache> taches = new HashSet<>();
    @PrePersist
    protected void onCreate() {
        creation = LocalDateTime.now();
        modification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modification = LocalDateTime.now();
    }

}
