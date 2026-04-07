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

    //tìm theo mã phân công
    @Query("SELECT r FROM AssessmentResult r " +
            "JOIN FETCH r.evaluationCriteria " +
            "JOIN FETCH r.assessmentRound " +
            "WHERE r.internshipAssignment.assignmentId = :assignmentId")
    List<AssessmentResult> findByAssignmentId(@Param("assignmentId") Long assignmentId);

    //lấy danh sách chấm điểm theo id
    @Query("SELECT r FROM AssessmentResult r " +
            "JOIN FETCH r.internshipAssignment a " +
            "JOIN FETCH a.student s " +
            "WHERE r.evaluatedBy.userId = :userId")
    List<AssessmentResult> findByEvaluatorId(@Param("userId") Long userId);

    //lấy toàn danh sách điểm của sinh viên
    @Query("SELECT r FROM AssessmentResult r " +
            "JOIN FETCH r.evaluationCriteria " +
            "JOIN FETCH r.assessmentRound " +
            "WHERE r.internshipAssignment.student.studentId = :studentId " +
            "ORDER BY r.assessmentRound.roundId ASC")
    List<AssessmentResult> findByStudentId(@Param("studentId") Long studentId);
}
