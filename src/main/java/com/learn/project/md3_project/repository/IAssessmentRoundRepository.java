package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.AssessmentRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAssessmentRoundRepository extends JpaRepository<AssessmentRound, Long> {
    List<AssessmentRound> findByInternshipPhase_PhaseId(Long phaseId);
}
