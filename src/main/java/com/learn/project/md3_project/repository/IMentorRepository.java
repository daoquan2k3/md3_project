package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMentorRepository extends JpaRepository<Mentor,Long> {

    // Tìm giảng viên hướng dẫn của một sinh viên cụ thể
    @Query("SELECT a.mentor FROM InternshipAssignment a WHERE a.student.studentId = :studentId")
    List<Mentor> findMentorsByStudentId(@Param("studentId") Long studentId);
}
