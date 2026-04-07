package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.AssessmentRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAssessmentRoundRepository extends JpaRepository<AssessmentRound, Long> {
    @Query("SELECT r FROM AssessmentRound r " +
            "LEFT JOIN FETCH r.internshipPhase " +
            "WHERE r.internshipPhase.phaseId = :phaseId " +
            "AND r.isActive = true")
    List<AssessmentRound> findByInternshipPhaseId(@Param("phaseId") Long phaseId);
}
