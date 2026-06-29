package com.example.demo.Repository;

import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Mestypes.TypeChantier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChantierRepository extends JpaRepository<Chantier, Long> {
    List <Chantier> findByType (TypeChantier type);
    List<Chantier> findByNomContainingIgnoreCase(String nom);
}
