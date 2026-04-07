package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.InternshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IInternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Long> {

    // Tìm tất cả sinh viên thuộc quyền quản lý của 1 Mentor
    @Query("SELECT a FROM InternshipAssignment a " +
            "JOIN FETCH a.student s " +
            "JOIN FETCH s.user " +
            "JOIN FETCH a.internshipPhase " +
            "WHERE a.mentor.mentorId = :mentorId")
    List<InternshipAssignment> findByMentorId(@Param("mentorId") Long mentorId);

    // Kiểm tra xem sinh viên đã được phân công cho giảng viên này hay chưa
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM InternshipAssignment a " +
            "WHERE a.student.studentId = :studentId " +
            "AND a.mentor.mentorId = :mentorId")
    boolean isMentorAssignedToStudent(@Param("studentId") Long studentId,@Param("mentorId") Long mentorId);

    //tìm danh sách phân công của 1 sinh viên
    @Query("SELECT a FROM InternshipAssignment a " +
            "JOIN FETCH a.mentor m " +
            "JOIN FETCH m.user " +
            "JOIN FETCH a.internshipPhase " +
            "WHERE a.student.studentId = :studentId")
    List<InternshipAssignment> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM InternshipAssignment a WHERE a.internshipPhase.phaseId = :phaseId")
    boolean existsByInternshipPhaseId(@Param("phaseId") Long phaseId);
}
