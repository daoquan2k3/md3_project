package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateRoundCriterionRequest;
import com.learn.project.md3_project.dto.request.UpdateRoundCriterionRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.RoundCriteriaResponse;
import com.learn.project.md3_project.entity.AssessmentRound;
import com.learn.project.md3_project.entity.EvaluationCriteria;
import com.learn.project.md3_project.entity.RoundCriteria;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IAssessmentRoundRepository;
import com.learn.project.md3_project.repository.IEvaluationCriteriaRepository;
import com.learn.project.md3_project.repository.IRoundCriteriaRepository;
import com.learn.project.md3_project.service.impl.IRoundCriteriaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoundCriteriaServiceImpl implements IRoundCriteriaService {
    private final IRoundCriteriaRepository roundCriteriaRepository;
    private final IAssessmentRoundRepository roundRepository;
    private final IEvaluationCriteriaRepository criteriaRepository;

    @Override
    public ApiResponse<List<RoundCriteriaResponse>> getAll(Long roundId) {
        List<RoundCriteria> list;
        if (roundId != null) {
            list = roundCriteriaRepository.findByRoundId(roundId);
        } else {
            list = roundCriteriaRepository.findAll();
        }

        List<RoundCriteriaResponse> responses = list.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses, "Thành công");
    }

    @Override
    public ApiResponse<RoundCriteriaResponse> getById(Long id) {
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi tiêu chí vòng ID: " + id));
        return ApiResponse.success(convertToResponse(rc), "Thành công");
    }

    @Override
    @Transactional
    public ApiResponse<RoundCriteriaResponse> create(CreateRoundCriterionRequest dto) {
        // Kiểm tra trùng lặp
        if (roundCriteriaRepository.existsByRoundAndCriteria(dto.getRoundId(), dto.getCriterionId())) {
            throw new RuntimeException("Tiêu chí này đã tồn tại trong vòng đánh giá!");
        }

        AssessmentRound round = roundRepository.findById(dto.getRoundId())
                .orElseThrow(() -> new ResourceNotFoundException("Round không tồn tại"));
        EvaluationCriteria criteria = criteriaRepository.findById(dto.getCriterionId())
                .orElseThrow(() -> new ResourceNotFoundException("Criterion không tồn tại"));

        RoundCriteria rc = RoundCriteria.builder()
                .assessmentRound(round)
                .evaluationCriteria(criteria)
                .weight(dto.getWeight())
                .build();

        return ApiResponse.success(convertToResponse(roundCriteriaRepository.save(rc)), "Thêm mới thành công");
    }

    @Override
    @Transactional
    public ApiResponse<RoundCriteriaResponse> update(Long id, UpdateRoundCriterionRequest dto) {
        RoundCriteria rc = roundCriteriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi ID: " + id));

        rc.setWeight(dto.getWeight());

        return ApiResponse.success(convertToResponse(roundCriteriaRepository.save(rc)), "Cập nhật trọng số thành công");
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(Long id) {
        if (!roundCriteriaRepository.existsById(id)) {
            throw new RuntimeException("Bản ghi không tồn tại");
        }
        roundCriteriaRepository.deleteById(id);
        return ApiResponse.success(null, "Xóa thành công");
    }

    private RoundCriteriaResponse convertToResponse(RoundCriteria rc) {
        return RoundCriteriaResponse.builder()
                .roundCriterionId(rc.getRoundCriterionId())
                .roundId(rc.getAssessmentRound().getRoundId())
                .roundName(rc.getAssessmentRound().getRoundName())
                .criterionId(rc.getEvaluationCriteria().getCriterionId())
                .criterionName(rc.getEvaluationCriteria().getCriterionName())
                .maxScore(rc.getEvaluationCriteria().getMaxScore())
                .weight(rc.getWeight())
                .build();
    }
}
