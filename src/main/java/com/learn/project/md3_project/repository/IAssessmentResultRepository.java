package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    boolean existsByAssessmentRound_RoundId(Long roundId);
    List<AssessmentResult> findByInternshipAssignment_AssignmentId(Long assignmentId);
    List<AssessmentResult> findByEvaluatedBy_UserId(Long userId);
    List<AssessmentResult> findByInternshipAssignment_Student_StudentId(Long studentId);
}
