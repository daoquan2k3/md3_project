package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateMentorRequest;
import com.learn.project.md3_project.dto.request.UpdateMentorRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.MentorResponse;
import com.learn.project.md3_project.entity.Mentor;
import com.learn.project.md3_project.entity.Role;
import com.learn.project.md3_project.entity.RoleName;
import com.learn.project.md3_project.entity.User;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IInternshipAssignmentRepository;
import com.learn.project.md3_project.repository.IMentorRepository;
import com.learn.project.md3_project.repository.IRoleRepository;
import com.learn.project.md3_project.repository.IUserRepository;
import com.learn.project.md3_project.security.principle.UserDetailCustom;
import com.learn.project.md3_project.service.impl.IMentorService;
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
@Transactional
@Slf4j
public class MentorServiceImpl implements IMentorService {
    private final ModelMapper modelMapper;
    private final IMentorRepository mentorRepository;
    private final IInternshipAssignmentRepository assignmentRepository;
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public ApiResponse<List<MentorResponse>> getAllMentorsByRole() {
        // 1. Lấy thông tin User hiện tại
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long currentUserId = currentUser.getUserId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStudent = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        List<Mentor> mentors;

        // 2. Lấy dữ liệu dựa trên quyền
        if (isAdmin) {
            // Admin xem toàn bộ giảng viên trong hệ thống
            mentors = mentorRepository.findAll();
        } else if (isStudent) {
            // Sinh viên chỉ xem được giảng viên đang hướng dẫn mình
            mentors = mentorRepository.findMentorsByStudentId(currentUserId);
        } else {
            throw new RuntimeException("Bạn không có quyền truy cập chức năng này!");
        }

        // 3. Map sang MentorResponse và lọc thông tin chi tiết
        List<MentorResponse> responses = mentors.stream()
                .map(mentor -> {
                    MentorResponse dto = modelMapper.map(mentor, MentorResponse.class);

                    if (mentor.getUser() != null) {
                        dto.setFullName(mentor.getUser().getFullName());

                        if (isAdmin) {
                            // Admin xem đầy đủ
                            dto.setEmail(mentor.getUser().getEmail());
                            dto.setPhone(mentor.getUser().getPhoneNumber());
                            dto.setIsActive(mentor.getUser().getIsActive());
                        } else {
                            // Student xem thông tin liên lạc để làm việc với giảng viên của mình
                            // (Ở đây thường sinh viên CẦN email/SĐT của giảng viên mình để liên hệ)
                            dto.setEmail(mentor.getUser().getEmail());
                            dto.setPhone(mentor.getUser().getPhoneNumber());

                            // Ẩn các thông tin quản trị hệ thống
                            dto.setIsActive(null);
                            dto.setCreatedAt(null);
                            dto.setUpdatedAt(null);
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(responses, isStudent ? "Lấy thông tin giảng viên hướng dẫn của bạn thành công" : "Lấy danh sách tất cả giảng viên thành công");
    }

    @Override
    public ApiResponse<MentorResponse> getMentorDetail(Long mentorId) {
        // 1. Lấy thông tin User hiện tại
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long currentUserId = currentUser.getUserId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMentor = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));
        boolean isStudent = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        // 2. Tìm Mentor trong DB
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên ID: " + mentorId));

        // 3. Kiểm tra quyền truy cập (Access Control)
        if (!isAdmin) {
            // Nếu là MENTOR, chỉ được xem ID của chính mình
            if (isMentor && !mentorId.equals(currentUserId)) {
                throw new RuntimeException("Bạn không có quyền xem thông tin của giảng viên khác!");
            }

            // Nếu là STUDENT, chỉ được xem nếu giảng viên này đang hướng dẫn mình
            if (isStudent) {
                boolean isAssigned = assignmentRepository.existsByStudent_StudentIdAndMentor_MentorId(currentUserId, mentorId);
                if (!isAssigned) {
                    throw new RuntimeException("Giảng viên này không thuộc danh sách hướng dẫn của bạn!");
                }
            }
        }

        // 4. Map sang MentorResponse và lọc dữ liệu (Data Filtering)
        MentorResponse response = modelMapper.map(mentor, MentorResponse.class);

        if (mentor.getUser() != null) {
            response.setFullName(mentor.getUser().getFullName());
            response.setDepartment(mentor.getDepartment());
            response.setAcademicRank(mentor.getAcademicRank());

            if (isAdmin || isMentor) {
                // ADMIN và CHÍNH MENTOR ĐÓ xem được đầy đủ
                response.setEmail(mentor.getUser().getEmail());
                response.setPhone(mentor.getUser().getPhoneNumber());
                response.setIsActive(mentor.getUser().getIsActive());
            } else if (isStudent) {
                // STUDENT chỉ xem được thông tin liên lạc để trao đổi công việc
                response.setEmail(mentor.getUser().getEmail());
                response.setPhone(mentor.getUser().getPhoneNumber());

                // Ẩn các thông tin quản trị
                response.setIsActive(null);
                response.setCreatedAt(null);
                response.setUpdatedAt(null);
            }
        }

        return ApiResponse.success(response, "Lấy thông tin chi tiết giảng viên thành công");
    }

    @Override
    public ApiResponse<MentorResponse> createMentor(CreateMentorRequest dto) {
        // 1. Kiểm tra email đã tồn tại trong hệ thống chưa
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        // 2. Lấy quyền ROLE_MENTOR từ DB
        Role mentorRole = roleRepository.findByRoleName(RoleName.ROLE_MENTOR)
                .orElseThrow(() -> new RuntimeException("Quyền ROLE_MENTOR không tồn tại!"));

        // 3. Tạo thực thể User
        User user = User.builder()
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPasswordHash()))
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhone())
                .roles(Set.of(mentorRole))
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // 4. Tạo thực thể Mentor (liên kết với User qua @MapsId)
        Mentor mentor = Mentor.builder()
                .user(savedUser) // studentId/mentorId sẽ lấy từ userId này
                .department(dto.getDepartment())
                .academicRank(dto.getAcademicRank())
                .build();

        Mentor savedMentor = mentorRepository.save(mentor);

        // 5. Truy vấn ngược lại hoặc gán thủ công để trả về Response
        MentorResponse response = modelMapper.map(savedMentor, MentorResponse.class);
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhoneNumber());
        response.setIsActive(savedUser.getIsActive());

        return ApiResponse.success(response, "Tạo tài khoản giáo viên hướng dẫn thành công");
    }

    @Override
    public ApiResponse<MentorResponse> updateMentor(Long mentorId, UpdateMentorRequest dto) {
        // 1. Lấy thông tin User hiện tại từ SecurityContext
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long currentUserId = currentUser.getUserId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 2. Kiểm tra quyền: Nếu không phải ADMIN thì ID thay đổi phải khớp với ID đang đăng nhập
        if (!isAdmin && !mentorId.equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật hồ sơ của giảng viên khác!");
        }

        // 3. Tìm kiếm Mentor
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên ID: " + mentorId));

        // 4. Cập nhật thông tin bảng Mentors
        if (dto.getDepartment() != null) mentor.setDepartment(dto.getDepartment());
        if (dto.getAcademicRank() != null) mentor.setAcademicRank(dto.getAcademicRank());

        // 5. Cập nhật thông tin bảng Users liên kết
        User user = mentor.getUser();
        if (user != null) {
            if (dto.getFullName() != null) user.setFullName(dto.getFullName());
            if (dto.getPhone() != null) user.setPhoneNumber(dto.getPhone());
            userRepository.save(user); // Lưu bảng User
        }

        Mentor savedMentor = mentorRepository.save(mentor); // Lưu bảng Mentor

        // 6. Map dữ liệu trả về MentorResponse
        MentorResponse response = modelMapper.map(savedMentor, MentorResponse.class);
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setIsActive(user.getIsActive());

        return ApiResponse.success(response, "Cập nhật thông tin giảng viên thành công");
    }
}
