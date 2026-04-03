package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.InternshipPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInternshipPhaseRepository extends JpaRepository<InternshipPhase, Long> {
    boolean existsByPhaseName(String name);
}
