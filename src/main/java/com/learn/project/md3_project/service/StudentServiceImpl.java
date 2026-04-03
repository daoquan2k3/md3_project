package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateStudentRequest;
import com.learn.project.md3_project.dto.request.UpdateStudentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.StudentResponse;
import com.learn.project.md3_project.entity.Role;
import com.learn.project.md3_project.entity.RoleName;
import com.learn.project.md3_project.entity.Student;
import com.learn.project.md3_project.entity.User;
import com.learn.project.md3_project.exception.AccessDeniedException;
import com.learn.project.md3_project.exception.DataExistException;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IInternshipAssignmentRepository;
import com.learn.project.md3_project.repository.IRoleRepository;
import com.learn.project.md3_project.repository.IStudentRepository;
import com.learn.project.md3_project.repository.IUserRepository;
import com.learn.project.md3_project.security.principle.UserDetailCustom;
import com.learn.project.md3_project.service.impl.IStudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements IStudentService {
    private final IStudentRepository studentRepository;
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final IInternshipAssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private UserDetailCustom getCurrentUser() {
        return (UserDetailCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private StudentResponse convertToResponse(Student student) {
        StudentResponse response = modelMapper.map(student, StudentResponse.class);
        if (student.getUser() != null) {
            response.setPhone(student.getUser().getPhoneNumber());
            response.setIsActive(student.getUser().getIsActive());
            response.setFullName(student.getUser().getFullName());
            response.setEmail(student.getUser().getEmail());
        }
        return response;
    }


    @Override
    public ApiResponse<List<StudentResponse>> getAllStudentsByRole() {
        UserDetailCustom currentUser = getCurrentUser();

        List<Student> students;
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            students = studentRepository.findAll();
        } else if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
            students = studentRepository.findAllByMentorId(currentUser.getUserId());
        } else {
            throw new AccessDeniedException("Bạn không có quyền truy cập danh sách này");
        }

        List<StudentResponse> responses = students.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách sinh viên thành công");
    }

    @Override
    public ApiResponse<StudentResponse> getStudentDetail(Long studentId) {
        UserDetailCustom currentUser = getCurrentUser();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên ID: " + studentId));

        validateAccess(currentUser, studentId);

        return ApiResponse.success(convertToResponse(student), "Lấy thông tin thành công");
    }

    private void validateAccess(UserDetailCustom currentUser, Long targetStudentId) {
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMentor = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));

        if (isAdmin) return;

        if (isMentor) {
            if (!assignmentRepository.existsByStudent_StudentIdAndMentor_MentorId(targetStudentId, currentUser.getUserId())) {
                throw new AccessDeniedException("Sinh viên này không thuộc danh sách hướng dẫn của bạn!");
            }
        } else { // Là Student
            if (!targetStudentId.equals(currentUser.getUserId())) {
                throw new AccessDeniedException("Bạn không thể xem hồ sơ của người khác!");
            }
        }
    }

    @Override
    @Transactional
    public ApiResponse<StudentResponse> createStudent(CreateStudentRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) throw new DataExistException("Email đã tồn tại!");
        if (studentRepository.existsByStudentCode(dto.getStudentCode())) throw new DataExistException("Mã SV đã tồn tại!");

        Role studentRole = roleRepository.findByRoleName(RoleName.ROLE_STUDENT)
                .orElseThrow(() -> new ResourceNotFoundException("Role không tồn tại"));

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPasswordHash()))
                .fullName(dto.getFullName())
                .roles(Set.of(studentRole))
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        Student student = modelMapper.map(dto, Student.class);
        student.setUser(savedUser);

        return ApiResponse.success(convertToResponse(studentRepository.save(student)), "Tạo mới thành công");
    }

    @Override
    @Transactional
    public ApiResponse<StudentResponse> updateStudent(Long studentId, UpdateStudentRequest dto) {
        validateAccess(getCurrentUser(), studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));

        modelMapper.map(dto, student);

        if (student.getUser() != null) {
            if (dto.getFullName() != null) student.getUser().setFullName(dto.getFullName());
            if (dto.getPhoneNumber() != null) student.getUser().setPhoneNumber(dto.getPhoneNumber());
        }

        return ApiResponse.success(convertToResponse(studentRepository.save(student)), "Cập nhật thành công");
    }
}