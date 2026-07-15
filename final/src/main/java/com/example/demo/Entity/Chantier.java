package com.example.demo.Entity;

import com.example.demo.Entity.Mestypes.StatusTache;
import com.example.demo.Entity.Mestypes.TypeChantier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity

@Getter
@Setter
public class Chantier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nom;

    private TypeChantier type;
    private String localisation;
    private String longitude;
    private String latitude;
    private String photo;
    private LocalDate datedebut;
    private LocalDate datefin ;
    private LocalDateTime creation;
    private LocalDateTime modification;
    @JsonIgnore
    @OneToMany(mappedBy="chantier",cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List <Tache> taches=new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "createur_id",nullable = false)
    private Utilisateur createur;
     @JsonIgnore
    @OneToMany(mappedBy = "chantier", cascade = CascadeType.ALL)
    private Set<Equipe> equipes = new HashSet<>();
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
