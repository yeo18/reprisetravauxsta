package com.example.demo.Repository;

import com.example.demo.Entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    Optional<Equipe> findByNom(String nom);

    List<Equipe> findByChantierIdIn(List<Long> chantierIds);
}
