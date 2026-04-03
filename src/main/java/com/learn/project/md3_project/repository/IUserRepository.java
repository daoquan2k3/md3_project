package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.RoleName;
import com.learn.project.md3_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findDistinctByRoles_RoleNameIn(Collection<RoleName> roleNames);
}
