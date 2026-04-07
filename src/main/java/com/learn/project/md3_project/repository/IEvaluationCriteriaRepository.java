package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IEvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM EvaluationCriteria c WHERE c.criterionName = :name")
    boolean existsByCriterionName(@Param("name") String criterionName);
}
