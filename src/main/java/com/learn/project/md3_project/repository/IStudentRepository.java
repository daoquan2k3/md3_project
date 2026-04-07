package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStudentRepository extends JpaRepository<Student,Long> {
    @Query("SELECT a.student FROM InternshipAssignment a WHERE a.mentor.mentorId = :mentorId")
    List<Student> findAllByMentorId(@Param("mentorId") Long mentorId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Student s WHERE s.studentCode = :studentCode")
    boolean existsByStudentCode(@Param("studentCode") String studentCode);
}
