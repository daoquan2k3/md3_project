package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateAssessmentResultRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentResultResponse;
import com.learn.project.md3_project.entity.*;
import com.learn.project.md3_project.exception.AccessDeniedException;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.*;
import com.learn.project.md3_project.security.principle.UserDetailCustom;
import com.learn.project.md3_project.service.impl.IAssessmentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentResultServiceImpl implements IAssessmentResultService {
    private final IAssessmentResultRepository resultRepository;
    private final IInternshipAssignmentRepository assignmentRepository;
    private final IAssessmentRoundRepository roundRepository;
    private final IEvaluationCriteriaRepository criteriaRepository;
    private final IUserRepository userRepository;

    @Override
    public ApiResponse<List<AssessmentResultResponse>> getAllResults(Long assignmentId) {
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMentor = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));

        List<AssessmentResult> results = resultRepository.findAllByRoleAndId(
                assignmentId,
                currentUser.getUserId(),
                isAdmin,
                isMentor
        );

        List<AssessmentResultResponse> responses = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách kết quả thành công");
    }

    @Override
    public ApiResponse<AssessmentResultResponse> createResult(CreateAssessmentResultRequest dto) {
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // Kiểm tra phân công: Mentor này có hướng dẫn sinh viên này không?
        InternshipAssignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin phân công"));

        if (!assignment.getMentor().getMentorId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("Bạn không có quyền chấm điểm cho sinh viên không thuộc danh sách hướng dẫn");
        }

        // Kiểm tra thời hạn vòng đánh giá
        AssessmentRound round = roundRepository.findById(dto.getRoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Vòng đánh giá không tồn tại"));
        LocalDate now = LocalDate.now();
        if (now.isBefore(round.getStartDate()) || now.isAfter(round.getEndDate())) {
            throw new RuntimeException("Hiện tại không nằm trong thời gian cho phép chấm điểm của vòng này");
        }

        // Kiểm tra điểm số tối đa
        EvaluationCriteria criteria = criteriaRepository.findById(dto.getCriterionId())
                .orElseThrow(() -> new RuntimeException("Tiêu chí không tồn tại"));
        if (dto.getScore().compareTo(criteria.getMaxScore()) > 0) {
            throw new RuntimeException("Điểm số không được vượt quá điểm tối đa: " + criteria.getMaxScore());
        }

        User evaluator = userRepository.findById(currentUser.getUserId()).get();

        AssessmentResult result = AssessmentResult.builder()
                .internshipAssignment(assignment)
                .assessmentRound(round)
                .evaluationCriteria(criteria)
                .score(dto.getScore())
                .comments(dto.getComments())
                .evaluatedBy(evaluator)
                .evaluationDate(LocalDateTime.now())
                .build();

        return ApiResponse.success(mapToResponse(resultRepository.save(result)), "Chấm điểm thành công");
    }

    @Override
    public ApiResponse<AssessmentResultResponse> updateResult(Long resultId, CreateAssessmentResultRequest dto) {
        UserDetailCustom currentUser = (UserDetailCustom) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        AssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kết quả đánh giá"));

        if (!result.getEvaluatedBy().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("Bạn chỉ có quyền chỉnh sửa kết quả do chính mình tạo ra");
        }

        result.setScore(dto.getScore());
        result.setComments(dto.getComments());
        result.setEvaluationDate(LocalDateTime.now());

        return ApiResponse.success(mapToResponse(resultRepository.save(result)), "Cập nhật kết quả thành công");
    }

    private AssessmentResultResponse mapToResponse(AssessmentResult res) {
        return AssessmentResultResponse.builder()
                .resultId(res.getResultId())
                .assignmentId(res.getInternshipAssignment().getAssignmentId())
                .studentName(res.getInternshipAssignment().getStudent().getUser().getFullName())
                .roundName(res.getAssessmentRound().getRoundName())
                .criterionName(res.getEvaluationCriteria().getCriterionName())
                .score(res.getScore())
                .comments(res.getComments())
                .evaluatorName(res.getEvaluatedBy().getFullName())
                .evaluationDate(res.getEvaluationDate())
                .build();
    }
}
