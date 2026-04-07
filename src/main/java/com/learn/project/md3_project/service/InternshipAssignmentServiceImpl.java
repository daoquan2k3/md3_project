package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.UpdateInternshipAsStatusRequest;
import com.learn.project.md3_project.dto.request.CreateInternshipAssignmentRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssignmentResponse;
import com.learn.project.md3_project.entity.InternshipAssignment;
import com.learn.project.md3_project.entity.InternshipPhase;
import com.learn.project.md3_project.entity.Mentor;
import com.learn.project.md3_project.entity.Student;
import com.learn.project.md3_project.exception.AccessDeniedException;
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


    private UserDetailCustom getCurrentUser() {
        return (UserDetailCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private AssignmentResponse convertToResponse(InternshipAssignment assignment) {
        AssignmentResponse response = modelMapper.map(assignment, AssignmentResponse.class);

        if (assignment.getStudent() != null && assignment.getStudent().getUser() != null) {
            response.setStudentName(assignment.getStudent().getUser().getFullName());
            response.setStudentCode(assignment.getStudent().getStudentCode());
        }
        if (assignment.getMentor() != null && assignment.getMentor().getUser() != null) {
            response.setMentorName(assignment.getMentor().getUser().getFullName());
        }
        if (assignment.getInternshipPhase() != null) {
            response.setPhaseName(assignment.getInternshipPhase().getPhaseName());
        }

        response.setStatus(assignment.getStatus().name());
        return response;
    }


    @Override
    @Transactional
    public ApiResponse<AssignmentResponse> createAssignment(CreateInternshipAssignmentRequest dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));
        Mentor mentor = mentorRepository.findById(dto.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
        InternshipPhase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn"));

        if (assignmentRepository.isMentorAssignedToStudent(dto.getStudentId(), dto.getPhaseId())) {
            throw new RuntimeException("Sinh viên đã có phân công trong giai đoạn này!");
        }

        InternshipAssignment assignment = InternshipAssignment.builder()
                .student(student)
                .mentor(mentor)
                .internshipPhase(phase)
                .status(InternshipAssignment.AssignmentStatus.PENDING)
                .build();

        return ApiResponse.success(convertToResponse(assignmentRepository.save(assignment)), "Phân công thực tập thành công");
    }

    @Override
    public ApiResponse<List<AssignmentResponse>> getAssignmentsByRole() {
        UserDetailCustom currentUser = getCurrentUser();
        Long userId = currentUser.getUserId();

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMentor = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));
        boolean isStudent = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        List<InternshipAssignment> assignments;
        if (isAdmin) {
            assignments = assignmentRepository.findAll();
        } else if (isMentor) {
            assignments = assignmentRepository.findByMentorId(userId);
        } else if (isStudent) {
            assignments = assignmentRepository.findByStudentId(userId);
        } else {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này");
        }

        List<AssignmentResponse> responses = assignments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách phân công thành công");
    }

    @Override
    public ApiResponse<AssignmentResponse> getAssignmentDetail(Long assignmentId) {
        UserDetailCustom currentUser = getCurrentUser();
        InternshipAssignment assign = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công ID: " + assignmentId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            boolean isOwner = assign.getMentor().getMentorId().equals(currentUser.getUserId()) ||
                    assign.getStudent().getStudentId().equals(currentUser.getUserId());
            if (!isOwner) throw new AccessDeniedException("Bạn không có quyền xem chi tiết phân công này!");
        }

        return ApiResponse.success(convertToResponse(assign), "Lấy chi tiết phân công thành công");
    }

    @Override
    @Transactional
    public ApiResponse<AssignmentResponse> updateAssignmentStatus(Long assignmentId, UpdateInternshipAsStatusRequest dto) {
        UserDetailCustom currentUser = getCurrentUser();
        InternshipAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công ID: " + assignmentId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !assignment.getMentor().getMentorId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật trạng thái cho phân công này!");
        }

        assignment.setStatus(dto.getStatus());
        return ApiResponse.success(convertToResponse(assignmentRepository.save(assignment)), "Cập nhật trạng thái thành công");
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Long phaseId) {
        log.info("Admin đang yêu cầu xóa giai đoạn thực tập ID: {}", phaseId);

        InternshipPhase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập ID: " + phaseId));

        boolean hasAssignments = assignmentRepository.existsByInternshipPhaseId(phaseId);

        if (hasAssignments) {
            phase.setActive(false);
            phaseRepository.save(phase);
            return ApiResponse.success(null, "Giai đoạn đã có dữ liệu phân công nên hệ thống đã chuyển sang trạng thái Ngưng hoạt động.");
        }

        phaseRepository.delete(phase);
        return ApiResponse.success(null, "Xóa giai đoạn thực tập thành công");
    }
}
