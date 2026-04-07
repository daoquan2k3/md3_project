package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateMentorRequest;
import com.learn.project.md3_project.dto.request.UpdateMentorRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.MentorResponse;
import com.learn.project.md3_project.entity.Mentor;
import com.learn.project.md3_project.entity.Role;
import com.learn.project.md3_project.entity.RoleName;
import com.learn.project.md3_project.entity.User;
import com.learn.project.md3_project.exception.AccessDeniedException;
import com.learn.project.md3_project.exception.DataExistException;
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


    private UserDetailCustom getCurrentUser() {
        return (UserDetailCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private MentorResponse convertToResponse(Mentor mentor, boolean isFullDetail) {
        MentorResponse response = modelMapper.map(mentor, MentorResponse.class);
        if (mentor.getUser() != null) {
            response.setFullName(mentor.getUser().getFullName());
            response.setEmail(mentor.getUser().getEmail());
            response.setPhone(mentor.getUser().getPhoneNumber());

            if (isFullDetail) {
                response.setIsActive(mentor.getUser().getIsActive());
            } else {
                response.setIsActive(null);
                response.setCreatedAt(null);
                response.setUpdatedAt(null);
            }
        }
        return response;
    }

    private void validateAccess(UserDetailCustom currentUser, Long targetMentorId) {
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMentor = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));

        if (isAdmin) return;

        if (isMentor && !targetMentorId.equals(currentUser.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền truy cập thông tin của giảng viên khác!");
        }
    }


    @Override
    public ApiResponse<List<MentorResponse>> getAllMentorsByRole() {
        UserDetailCustom currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStudent = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        List<Mentor> mentors;
        if (isAdmin || isStudent) {
            mentors = mentorRepository.findAll();
        } else {
            throw new AccessDeniedException("Bạn không có quyền truy cập danh sách này!");
        }

        List<MentorResponse> responses = mentors.stream()
                .map(m -> convertToResponse(m, isAdmin))
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách giảng viên thành công");
    }

    @Override
    public ApiResponse<MentorResponse> getMentorDetail(Long mentorId) {
        UserDetailCustom currentUser = getCurrentUser();
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên ID: " + mentorId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStudent = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        // Kiểm tra quyền xem
        if (!isAdmin && !mentorId.equals(currentUser.getUserId())) {
            if (isStudent) {
                boolean isAssigned = assignmentRepository.isMentorAssignedToStudent(currentUser.getUserId(), mentorId);
                if (!isAssigned) throw new AccessDeniedException("Giảng viên này không hướng dẫn bạn!");
            } else {
                throw new AccessDeniedException("Truy cập bị từ chối!");
            }
        }

        return ApiResponse.success(convertToResponse(mentor, !isStudent), "Lấy thông tin chi tiết thành công");
    }

    @Override
    public ApiResponse<MentorResponse> createMentor(CreateMentorRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) throw new DataExistException("Email đã tồn tại!");

        Role mentorRole = roleRepository.findByRoleName(RoleName.ROLE_MENTOR)
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_MENTOR không tồn tại!"));

        User user = modelMapper.map(dto, User.class);
        user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));
        user.setRoles(Set.of(mentorRole));
        user.setIsActive(true);
        user.setIsDeleted(false);
        User savedUser = userRepository.save(user);

        Mentor mentor = modelMapper.map(dto, Mentor.class);
        mentor.setUser(savedUser);
        Mentor savedMentor = mentorRepository.save(mentor);

        return ApiResponse.success(convertToResponse(savedMentor, true), "Tạo giảng viên thành công");
    }

    @Override
    public ApiResponse<MentorResponse> updateMentor(Long mentorId, UpdateMentorRequest dto) {
        validateAccess(getCurrentUser(), mentorId);

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));

        modelMapper.map(dto, mentor);

        User user = mentor.getUser();
        if (user != null) {
            if (dto.getFullName() != null && !dto.getFullName().isBlank()) user.setFullName(dto.getFullName());
            if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) user.setPhoneNumber(dto.getPhoneNumber());
            userRepository.save(user);
        }

        return ApiResponse.success(convertToResponse(mentorRepository.save(mentor), true), "Cập nhật thành công");
    }
}
