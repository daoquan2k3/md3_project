package com.learn.project.md3_project.repository;

import com.learn.project.md3_project.entity.RoleName;
import com.learn.project.md3_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    //tìm theo email
    Optional<User> findByEmail(@Param("email") String email);
    //kiểm tra email
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
            "WHERE u.email = :email AND u.isDeleted = false")
    boolean existsByEmail(@Param("email") String email);
    //lấy danh sách người dùng theo vai trò
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r " +
            "WHERE r.roleName IN :roleNames AND u.isDeleted = false")
    List<User> findUserByRoles(@Param("roleNames") Collection<RoleName> roleNames);
}
