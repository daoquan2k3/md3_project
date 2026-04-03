package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long> {
    boolean existsByCriterionName(String criterionName);
}
