package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
//
    //kiểm tra tồn tại
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM AssessmentResult r WHERE r.assessmentRound.roundId = :roundId")
    boolean existsByRoundId(@Param("roundId") Long roundId);

    @Query("SELECT r FROM AssessmentResult r " +
            "JOIN FETCH r.evaluationCriteria " +
            "JOIN FETCH r.assessmentRound " +
            "JOIN FETCH r.internshipAssignment a " +
            "JOIN FETCH a.student s " +
            "WHERE " +
            //Admin: Xem tất cả hoặc theo assignmentId
            "(:isAdmin = true AND (:assignmentId IS NULL OR a.assignmentId = :assignmentId)) " +
            "OR " +
            //Mentor: Chỉ xem kết quả do mình chấm (:userId là ID của Mentor)
            "(:isMentor = true AND r.evaluatedBy.userId = :userId AND (:assignmentId IS NULL OR a.assignmentId = :assignmentId)) " +
            "OR " +
            //Student: Chỉ xem kết quả của chính mình (:userId là ID của Student)
            "(:isAdmin = false AND :isMentor = false AND s.studentId = :userId)")
    List<AssessmentResult> findAllByRoleAndId(
            @Param("assignmentId") Long assignmentId,
            @Param("userId") Long userId,
            @Param("isAdmin") boolean isAdmin,
            @Param("isMentor") boolean isMentor
    );
}
