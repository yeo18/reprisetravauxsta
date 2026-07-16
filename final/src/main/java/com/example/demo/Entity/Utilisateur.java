package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Size(min = 2,max = 40)
    private String nom;
    @Column(nullable = false)
    @Size(min = 2,max = 75)
    private String prenom;
    @Column(nullable = false,unique = true)
    @Size(min = 7,max = 75)
    private String email;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String telephone;
    private LocalDateTime  datecreation;
    @Column(nullable = false)
    private LocalDateTime dateModification;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="Utilisateur_permision",
        joinColumns=@JoinColumn(name="utilisateur_id"),
        inverseJoinColumns=@JoinColumn(name="permission_id")
    )
    private Set<Permission> permissionsSpecifiques= new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profil_id", nullable = false)
    private Profil profil;
    @JsonIgnore
    @OneToMany(mappedBy = "assigneA", fetch=FetchType.LAZY)
    private List<Tache> tachesassignees=new ArrayList<>();
    @JsonIgnore
    @OneToMany
    private List <Chantier> chantierCreer=new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    @JsonIgnoreProperties({"membres", "taches", "chantier", "creation", "modification"})
    private Equipe equipe;
    @ManyToMany(mappedBy = "utilisateursAssiges", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Chantier> chantiersAssiges = new HashSet<>();
    @PrePersist
    protected void onCreate() {
        this.datecreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }


}
