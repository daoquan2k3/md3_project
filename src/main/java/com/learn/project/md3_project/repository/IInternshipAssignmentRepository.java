package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.InternshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IInternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Long> {

    // Tìm tất cả sinh viên thuộc quyền quản lý của 1 Mentor
    List<InternshipAssignment> findByMentor_MentorId(Long mentorId);

    // Kiểm tra xem sinh viên đã được phân công trong phase này chưa
    boolean existsByStudent_StudentIdAndInternshipPhase_PhaseId(Long studentId, Long phaseId);

    List<InternshipAssignment> findByStudent_StudentId(Long studentId);


    boolean existsByStudent_StudentIdAndMentor_MentorId(Long studentId, Long mentorId);

    boolean existsByInternshipPhase_PhaseId(Long phaseId);
}
