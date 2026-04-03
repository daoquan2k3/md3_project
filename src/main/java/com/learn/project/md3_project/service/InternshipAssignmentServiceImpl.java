package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.AsignStatusUpdateRequest;
import com.learn.project.md3_project.dto.request.AssignmentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssignmentResponse;
import com.learn.project.md3_project.entity.InternshipAssignment;
import com.learn.project.md3_project.entity.InternshipPhase;
import com.learn.project.md3_project.entity.Mentor;
import com.learn.project.md3_project.entity.Student;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IInternshipAssignmentRepository;
import com.learn.project.md3_project.repository.IInternshipPhaseRepository;
import com.learn.project.md3_project.repository.IMentorRepository;
import com.learn.project.md3_project.repository.IStudentRepository;
import com.learn.project.md3_project.security.principle.UserDetailCustom;
import com.learn.project.md3_project.service.impl.IInternshipAssignmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipAssignmentServiceImpl implements IInternshipAssignmentService {
    private final IInternshipAssignmentRepository assignmentRepository;
    private final IStudentRepository studentRepository;
    private final IMentorRepository mentorRepository;
    private final IInternshipPhaseRepository phaseRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ApiResponse<AssignmentResponse> createAssignment(AssignmentRequest dto) throws  ResourceNotFoundException {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));
        Mentor mentor = mentorRepository.findById(dto.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
        InternshipPhase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn"));

        if (assignmentRepository.existsByStudent_StudentIdAndInternshipPhase_PhaseId(dto.getStudentId(), dto.getPhaseId())) {
            throw new RuntimeException("Sinh viên đã có phân công trong giai đoạn này!");
        }

        InternshipAssignment assignment = InternshipAssignment.builder()
                .student(student)
                .mentor(mentor)
                .internshipPhase(phase)
                .status(InternshipAssignment.AssignmentStatus.PENDING)
                .build();

        InternshipAssignment saved = assignmentRepository.save(assignment);

        AssignmentResponse response = modelMapper.map(saved, AssignmentResponse.class);
        response.setStudentName(student.getUser().getFullName());
        response.setStudentCode(student.getStudentCode());
        response.setMentorName(mentor.getUser().getFullName());
        response.setPhaseName(phase.getPhaseName());
        response.setStatus(saved.getStatus().name());

        return ApiResponse.success(response, "Phân công thực tập thành công");
    }

    @Override
    public ApiResponse<List<AssignmentResponse>> getAssignmentsByRole() {
        // 1. Lấy thông tin User đang đăng nhập từ SecurityContext
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long currentUserId = currentUser.getUserId();

        // Kiểm tra các quyền của User
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMentor = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));
        boolean isStudent = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        List<InternshipAssignment> assignments;

        // 2. Phân luồng lấy dữ liệu dựa trên Role
        if (isAdmin) {
            assignments = assignmentRepository.findAll();
        } else if (isMentor) {
            assignments = assignmentRepository.findByMentor_MentorId(currentUserId);
        } else if (isStudent) {
            assignments = assignmentRepository.findByStudent_StudentId(currentUserId);
        } else {
            return ApiResponse.success(new ArrayList<>(), "Không có quyền truy cập dữ liệu");
        }

        List<AssignmentResponse> responses = assignments.stream()
                .map(item -> {
                    AssignmentResponse dto = modelMapper.map(item, AssignmentResponse.class);

                    // Gán thủ công các trường lồng nhau (Nested Objects)
                    dto.setStudentName(item.getStudent().getUser().getFullName());
                    dto.setStudentCode(item.getStudent().getStudentCode());
                    dto.setMentorName(item.getMentor().getUser().getFullName());
                    dto.setPhaseName(item.getInternshipPhase().getPhaseName());
                    dto.setStatus(item.getStatus().name());

                    return dto;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách phân công thành công");
    }

    @Override
    public ApiResponse<AssignmentResponse> getAssignmentDetail(Long assignmentId) {
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long currentUserId = currentUser.getUserId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        InternshipAssignment assign = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + assignmentId));

        if (!isAdmin) {
            boolean isMentorOfThis = assign.getMentor().getMentorId().equals(currentUserId);
            boolean isStudentOfThis = assign.getStudent().getStudentId().equals(currentUserId);

            if (!isMentorOfThis && !isStudentOfThis) {
                throw new RuntimeException("Bạn không có quyền xem chi tiết phân công này!");
            }
        }

        AssignmentResponse response = modelMapper.map(assign, AssignmentResponse.class);

        // Gán thủ công các trường thông tin từ User liên quan
        response.setStudentName(assign.getStudent().getUser().getFullName());
        response.setStudentCode(assign.getStudent().getStudentCode());
        response.setMentorName(assign.getMentor().getUser().getFullName());
        response.setPhaseName(assign.getInternshipPhase().getPhaseName());
        response.setStatus(assign.getStatus().name());

        return ApiResponse.success(response, "Lấy chi tiết phân công thành công");
    }

    @Override
    public ApiResponse<AssignmentResponse> updateAssignmentStatus(Long assignmentId, AsignStatusUpdateRequest dto) {
        // 1. Lấy thông tin User đang đăng nhập
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long currentUserId = currentUser.getUserId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 2. Tìm bản phân công
        InternshipAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công ID: " + assignmentId));

        // 3. KIỂM TRA QUYỀN: Nếu là Mentor, chỉ được sửa sinh viên mình hướng dẫn
        if (!isAdmin) {
            boolean isMentorOfThis = assignment.getMentor().getMentorId().equals(currentUserId);
            if (!isMentorOfThis) {
                throw new RuntimeException("Bạn không có quyền cập nhật trạng thái cho phân công này!");
            }
        }

        // 4. Cập nhật trạng thái mới
        log.info("Cập nhật trạng thái phân công {} từ {} sang {}",
                assignmentId, assignment.getStatus(), dto.getStatus());

        assignment.setStatus(dto.getStatus());
        InternshipAssignment updated = assignmentRepository.save(assignment);

        // 5. Trả về kết quả qua ModelMapper
        AssignmentResponse response = modelMapper.map(updated, AssignmentResponse.class);

        // Gán thủ công các thông tin định danh
        response.setStudentName(updated.getStudent().getUser().getFullName());
        response.setMentorName(updated.getMentor().getUser().getFullName());
        response.setPhaseName(updated.getInternshipPhase().getPhaseName());
        response.setStatus(updated.getStatus().name());

        return ApiResponse.success(response, "Cập nhật trạng thái thành công");
    }
}
