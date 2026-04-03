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

//@Service
//@RequiredArgsConstructor
//public class StudentServiceImpl implements IStudentService {
//    private final IStudentRepository studentRepository;
//    private final IRoleRepository roleRepository;
//    private final IUserRepository userRepository;
//    private final IInternshipAssignmentRepository assignmentRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final ModelMapper modelMapper;
//
//    @Override
//    public ApiResponse<List<StudentResponse>> getAllStudentsByRole() {
//        // 1. Lấy thông tin User hiện tại từ SecurityContext
//        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        Long currentUserId = currentUser.getUserId();
//        boolean isAdmin = currentUser.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//        boolean isMentor = currentUser.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));
//
//        List<Student> students;
//
//        // 2. Lấy dữ liệu theo Role (Sử dụng Repository đã viết ở bước trước)
//        if (isAdmin) {
//            students = studentRepository.findAll();
//        } else if (isMentor) {
//            students = studentRepository.findAllByMentorId(currentUserId);
//        } else {
//            throw new AccessDeniedException("Bạn không có quyền truy cập danh sách này");
//        }
//
//        // 3. Map sang StudentResponse
//        List<StudentResponse> responses = students.stream()
//                .map(student -> {
//                    // Sử dụng ModelMapper cho các trường trùng tên
//                    StudentResponse dto = modelMapper.map(student, StudentResponse.class);
//
//                    // Gán các trường lấy từ thực thể User liên kết thông qua @OneToOne
//                    if (student.getUser() != null) {
//                        dto.setFullName(student.getUser().getFullName());
//                        dto.setEmail(student.getUser().getEmail());
//                        dto.setPhone(student.getUser().getPhoneNumber());
//                        dto.setIsActive(student.getUser().getIsActive());
//                    }
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        return ApiResponse.success(responses, "Lấy danh sách sinh viên thành công");
//    }
//
//    @Override
//    public ApiResponse<StudentResponse> getStudentDetail(Long studentId) {
//        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        Long currentUserId = currentUser.getUserId();
//        boolean isAdmin = currentUser.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//        boolean isMentor = currentUser.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));
//
//        Student student = studentRepository.findById(studentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + studentId));
//
//        if (!isAdmin) {
//            // Nếu là sinh viên, chỉ được xem ID của chính mình
//            if (!isMentor && !studentId.equals(currentUserId)) {
//                throw new AccessDeniedException("Bạn không có quyền xem thông tin sinh viên này!");
//            }
//
//            // Nếu là Mentor, chỉ được xem sinh viên mình hướng dẫn
//            if (isMentor && !assignmentRepository.existsByStudent_StudentIdAndMentor_MentorId(studentId, currentUserId)) {
//                throw new AccessDeniedException("Sinh viên này không thuộc danh sách hướng dẫn của bạn!");
//            }
//        }
//
//        StudentResponse response = modelMapper.map(student, StudentResponse.class);
//        if (student.getUser() != null) {
//            response.setFullName(student.getUser().getFullName());
//            response.setEmail(student.getUser().getEmail());
//            response.setPhone(student.getUser().getPhoneNumber());
//            response.setIsActive(student.getUser().getIsActive());
//        }
//
//        return ApiResponse.success(response, "Lấy thông tin chi tiết sinh viên thành công");
//    }
//
//    @Override
//    public ApiResponse<StudentResponse> createStudent(CreateStudentRequest dto) {
//        // 1. Kiểm tra email và mã sinh viên đã tồn tại chưa
//        if (userRepository.existsByEmail(dto.getEmail())) {
//            throw new DataExistException("Email đã được sử dụng!");
//        }
//        if (studentRepository.existsByStudentCode(dto.getStudentCode())) {
//            throw new DataExistException("Mã sinh viên đã tồn tại!");
//        }
//
//        // 2. Tạo User mới với ROLE_STUDENT
//        Role studentRole = roleRepository.findByRoleName(RoleName.ROLE_STUDENT)
//                .orElseThrow(() -> new ResourceNotFoundException("Quyền ROLE_STUDENT không tồn tại trong hệ thống"));
//
//        User user = User.builder()
//                .email(dto.getEmail())
//                .passwordHash(passwordEncoder.encode(dto.getPasswordHash()))
//                .fullName(dto.getFullName())
//                .roles(Set.of(studentRole))
//                .isActive(true)
//                .build();
//
//        User savedUser = userRepository.save(user);
//
//        // 3. Tạo hồ sơ Student liên kết với User vừa tạo
//        Student student = Student.builder()
//                .user(savedUser) // @MapsId sẽ lấy ID của savedUser gán cho studentId
//                .studentCode(dto.getStudentCode())
//                .major(dto.getMajor())
//                .studentClass(dto.getStudentClass())
//                .dateOfBirth(dto.getDateOfBirth())
//                .address(dto.getAddress())
//                .build();
//
//        Student savedStudent = studentRepository.save(student);
//
//        // 4. Map sang StudentResponse trả về
//        StudentResponse response = modelMapper.map(savedStudent, StudentResponse.class);
//        response.setFullName(savedUser.getFullName());
//        response.setEmail(savedUser.getEmail());
//
//        return ApiResponse.success(response, "Tạo sinh viên mới thành công");
//    }
//
//    @Override
//    public ApiResponse<StudentResponse> updateStudent(Long studentId, UpdateStudentRequest dto) {
//        // 1. Lấy thông tin User hiện tại từ SecurityContext để kiểm tra quyền chính chủ
//        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        Long currentUserId = currentUser.getUserId();
//        boolean isAdmin = currentUser.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//
//        // 2. Kiểm tra quyền: Nếu không phải ADMIN thì ID thay đổi phải trùng với ID đang đăng nhập
//        if (!isAdmin && !studentId.equals(currentUserId)) {
//            throw new AccessDeniedException("Bạn không có quyền cập nhật hồ sơ của người khác!");
//        }
//
//        // 3. Tìm kiếm sinh viên
//        Student student = studentRepository.findById(studentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên ID: " + studentId));
//
//        // 4. Cập nhật thông tin bảng Students (Chỉ cập nhật nếu không null)
//        if (dto.getMajor() != null) student.setMajor(dto.getMajor());
//        if (dto.getStudentClass() != null) student.setStudentClass(dto.getStudentClass());
//        if (dto.getDateOfBirth() != null) student.setDateOfBirth(dto.getDateOfBirth());
//        if (dto.getAddress() != null) student.setAddress(dto.getAddress());
//
//        // 5. Cập nhật thông tin bảng Users liên kết
//        User user = student.getUser();
//        if (user != null) {
//            if (dto.getFullName() != null) user.setFullName(dto.getFullName());
//            if (dto.getPhone() != null) user.setPhoneNumber(dto.getPhone());
//            userRepository.save(user);
//        }
//
//        Student savedStudent = studentRepository.save(student);
//
//        // 6. Map sang StudentResponse
//        StudentResponse response = modelMapper.map(savedStudent, StudentResponse.class);
//        response.setFullName(user.getFullName());
//        response.setPhone(user.getPhoneNumber());
//        response.setEmail(user.getEmail());
//
//        return ApiResponse.success(response, "Cập nhật hồ sơ sinh viên thành công");
//    }
//}


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

        // Kiểm tra quyền (Refactored logic)
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
            if (dto.getPhone() != null) student.getUser().setPhoneNumber(dto.getPhone());
        }

        return ApiResponse.success(convertToResponse(studentRepository.save(student)), "Cập nhật thành công");
    }
}