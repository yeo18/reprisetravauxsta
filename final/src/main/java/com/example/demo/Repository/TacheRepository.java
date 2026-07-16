package com.example.demo.Repository;

import com.example.demo.Entity.Mestypes.Priorite;
import com.example.demo.Entity.Mestypes.StatusTache;
import com.example.demo.Entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    List<Tache> findByChantierIdIn(List<Long> chantierIds);

    List<Tache> findByAssigneAId(Long utilisateurId);

    List<Tache> findByEquipes_Id(Long equipeId);

    @Query("SELECT DISTINCT t FROM Tache t LEFT JOIN t.equipes e WHERE t.assigneA.id = :userId OR e.id IN :equipeIds")
    List<Tache> findByAssigneAIdOrEquipesIdIn(@Param("userId") Long userId, @Param("equipeIds") List<Long> equipeIds);

    @Query("SELECT DISTINCT t FROM Tache t LEFT JOIN t.equipes e WHERE t.chantier.id = :chantierId AND (t.assigneA.id = :userId OR e.id IN :equipeIds)")
    List<Tache> findByChantierIdAndAssigneAIdOrEquipesIdIn(@Param("chantierId") Long chantierId, @Param("userId") Long userId, @Param("equipeIds") List<Long> equipeIds);

    @Query("SELECT DISTINCT t FROM Tache t LEFT JOIN t.equipes e WHERE (t.assigneA.id = :userId OR e.id IN :equipeIds) AND t.datedebut BETWEEN :start AND :end")
    List<Tache> findByAssigneAIdOrEquipesIdInAndDatedebutBetween(@Param("userId") Long userId, @Param("equipeIds") List<Long> equipeIds, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT DISTINCT t FROM Tache t LEFT JOIN t.equipes e WHERE (t.assigneA.id = :userId OR e.id IN :equipeIds) AND t.datefin BETWEEN :start AND :end")
    List<Tache> findByAssigneAIdOrEquipesIdInAndDatefinBetween(@Param("userId") Long userId, @Param("equipeIds") List<Long> equipeIds, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT DISTINCT t FROM Tache t LEFT JOIN t.equipes e WHERE t.chantier.id = :chantierId AND (t.assigneA.id = :userId OR e.id IN :equipeIds) AND t.datedebut BETWEEN :start AND :end")
    List<Tache> findByChantierIdAndAssigneAIdOrEquipesIdInAndDatedebutBetween(@Param("chantierId") Long chantierId, @Param("userId") Long userId, @Param("equipeIds") List<Long> equipeIds, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT DISTINCT t FROM Tache t LEFT JOIN t.equipes e WHERE t.chantier.id = :chantierId AND (t.assigneA.id = :userId OR e.id IN :equipeIds) AND t.datefin BETWEEN :start AND :end")
    List<Tache> findByChantierIdAndAssigneAIdOrEquipesIdInAndDatefinBetween(@Param("chantierId") Long chantierId, @Param("userId") Long userId, @Param("equipeIds") List<Long> equipeIds, @Param("start") LocalDate start, @Param("end") LocalDate end);
}