package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.InternshipPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IInternshipPhaseRepository extends JpaRepository<InternshipPhase, Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM InternshipPhase p WHERE p.phaseName = :phaseName")
    boolean existsByPhaseName(@Param("name") String phaseName);
}
