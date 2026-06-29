package com.example.demo.Repository;

import com.example.demo.Entity.Mestypes.Priorite;
import com.example.demo.Entity.Mestypes.StatusTache;
import com.example.demo.Entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TacheRepository extends JpaRepository<Tache, Long> {

    List<Tache> findByStatus(StatusTache status);

    List<Tache> findByChantierIdAndPriorite(
            Long chantierId,
            Priorite priorite
    );

    long countByChantierId(Long chantierId);

    long countByChantierIdAndStatus(
            Long chantierId,
            StatusTache status
    );

    List<Tache> findByChantierId(Long chantierId);
}