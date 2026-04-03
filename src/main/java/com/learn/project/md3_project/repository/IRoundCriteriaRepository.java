package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.RoundCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRoundCriteriaRepository extends JpaRepository<RoundCriteria, Long> {
    List<RoundCriteria> findByAssessmentRound_RoundId(Long roundId);
    boolean existsByEvaluationCriteria_CriterionId(Long criterionId);

    //void deleteAllByAssessmentRound_RoundId(Long roundId);

    Optional<RoundCriteria> findByAssessmentRound_RoundIdAndEvaluationCriteria_CriterionId(Long roundId, Long criterionId);

    boolean existsByAssessmentRound_RoundIdAndEvaluationCriteria_CriterionId(Long roundId, Long criterionId);

    @Modifying
    @Query("DELETE FROM RoundCriteria rc WHERE rc.assessmentRound.roundId = :roundId")
    void deleteAllByAssessmentRound_RoundId(Long roundId);
}
