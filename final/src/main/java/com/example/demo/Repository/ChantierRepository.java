package com.example.demo.Repository;

import com.example.demo.Entity.Chantier;
import com.example.demo.Entity.Mestypes.TypeChantier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChantierRepository extends JpaRepository<Chantier, Long> {
    List <Chantier> findByType (TypeChantier type);
    List<Chantier> findByNomContainingIgnoreCase(String nom);
    Optional<Chantier> findByNom(String nom);

    @Query("SELECT c FROM Chantier c JOIN c.utilisateursAssiges u WHERE u.id = :userId")
    List<Chantier> findChantiersByUtilisateurId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Chantier c JOIN c.utilisateursAssiges u WHERE c.id = :chantierId AND u.id = :userId")
    boolean isUtilisateurAssignedToChantier(@Param("chantierId") Long chantierId, @Param("userId") Long userId);
}
