package com.example.demo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Profil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=3,max=75,message = "le nom doit etre compris entre 3 et 75 lettres")
    @Column(unique = true,nullable = false)
    private String nom;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "profil_permission",
            joinColumns = @JoinColumn(name="profil_id"),
            inverseJoinColumns = @JoinColumn(name="permission_id")

    )
    private Set<Permission> permissions=new HashSet<>();
}
