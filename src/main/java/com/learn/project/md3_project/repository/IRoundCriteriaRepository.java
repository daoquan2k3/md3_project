package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.RoundCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRoundCriteriaRepository extends JpaRepository<RoundCriteria, Long> {
    //tìm theo vòng đánh giá
    @Query("SELECT rc FROM RoundCriteria rc " +
            "JOIN FETCH rc.evaluationCriteria " +
            "WHERE rc.assessmentRound.roundId = :roundId")
    List<RoundCriteria> findByRoundId(@Param("roundId") Long roundId);

    //ktra tồn tại
    @Query("SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END " +
            "FROM RoundCriteria rc WHERE rc.evaluationCriteria.criterionId = :criterionId")
    boolean existsByCriterionId(@Param("criterionId") Long criterionId);

    @Query("SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END " +
            "FROM RoundCriteria rc " +
            "WHERE rc.assessmentRound.roundId = :roundId " +
            "AND rc.evaluationCriteria.criterionId = :criterionId")
    boolean existsByRoundAndCriteria(@Param("roundId") Long roundId, @Param("criterionId") Long criterionId);

    @Modifying
    @Query("DELETE FROM RoundCriteria rc WHERE rc.assessmentRound.roundId = :roundId")
    void deleteAllByAssessmentRoundId(@Param("roundId") Long roundId);
}
