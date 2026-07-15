package com.example.demo.Entity;

import com.example.demo.Entity.Mestypes.Priorite;
import com.example.demo.Entity.Mestypes.StatusTache;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Tache {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(nullable = false)
    private String titre;
    private String description;
    private Priorite  priorite;
    private StatusTache status;
    @JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate datedebut;
    @JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate datefin ;
    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    private LocalDateTime creation;
    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    private LocalDateTime modification;


    @Column(name = "cout_estime")
    private Double coutEstime;

    @Column(name = "duree_estimee_annees")
    private Integer dureeEstimeeAnnees = 0;

    @Column(name = "duree_estimee_mois")
    private Integer dureeEstimeeMois = 0;

    @Column(name = "duree_estimee_jours")
    private Integer dureeEstimeeJours = 0;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="chantier_id",nullable = false)
    private Chantier chantier;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigne_a_id")
    private Utilisateur assigneA;
    // Une tâche peut être assignée à plusieurs équipes
    @ManyToMany(mappedBy = "taches")
    private Set<Equipe> equipes = new HashSet<>();
    public String getDureeEstimeeFormate() {
        StringBuilder sb = new StringBuilder();
        if (dureeEstimeeAnnees > 0) sb.append(dureeEstimeeAnnees).append(" an(s) ");
        if (dureeEstimeeMois > 0) sb.append(dureeEstimeeMois).append(" mois ");
        if (dureeEstimeeJours > 0) sb.append(dureeEstimeeJours).append(" jour(s)");
        return sb.toString().trim();
    }
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
