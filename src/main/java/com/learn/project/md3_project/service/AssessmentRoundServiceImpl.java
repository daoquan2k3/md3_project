package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateAssessmentRoundRequest;
import com.learn.project.md3_project.dto.request.RoundCriterionRequest;
import com.learn.project.md3_project.dto.request.UpdateAssessmentRoundRequest;
import com.learn.project.md3_project.dto.request.UpdateWeightRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentRoundResponse;
import com.learn.project.md3_project.dto.response.RoundCriteriaResponse;
import com.learn.project.md3_project.entity.AssessmentRound;
import com.learn.project.md3_project.entity.EvaluationCriteria;
import com.learn.project.md3_project.entity.InternshipPhase;
import com.learn.project.md3_project.entity.RoundCriteria;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.*;
import com.learn.project.md3_project.service.impl.IAssessmentRoundService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentRoundServiceImpl implements IAssessmentRoundService {
    private final IAssessmentRoundRepository roundRepository;
    private final IInternshipPhaseRepository phaseRepository;
    private final IEvaluationCriteriaRepository criteriaRepository;
    private final IRoundCriteriaRepository roundCriteriaRepository;
    private final IAssessmentResultRepository resultRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<List<AssessmentRoundResponse>> getAllRounds(Long phaseId) {
        List<AssessmentRound> rounds;
        if (phaseId != null) {
            rounds = roundRepository.findByInternshipPhase_PhaseId(phaseId);
        } else {
            rounds = roundRepository.findAll();
        }

        List<AssessmentRoundResponse> responses = rounds.stream()
                .map(r -> modelMapper.map(r, AssessmentRoundResponse.class))
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách đợt đánh giá thành công");
    }

    @Override
    public ApiResponse<AssessmentRoundResponse> getRoundDetail(Long id) {
        // 1. Tìm vòng đánh giá
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đánh giá ID: " + id));

        // 2. Map thông tin cơ bản của Round
        AssessmentRoundResponse response = modelMapper.map(round, AssessmentRoundResponse.class);

        // 3. Lấy danh sách tiêu chí đã gán cho vòng này
        List<RoundCriteria> roundCriteriaList = roundCriteriaRepository.findByAssessmentRound_RoundId(id);

        // 4. Chuyển đổi sang List<RoundCriteriaResponse>
        List<RoundCriteriaResponse> criteriaResponses = roundCriteriaList.stream()
                .map(rc -> RoundCriteriaResponse.builder()
                        .criterionId(rc.getEvaluationCriteria().getCriterionId())
                        .criterionName(rc.getEvaluationCriteria().getCriterionName())
                        .maxScore(rc.getEvaluationCriteria().getMaxScore())
                        .weight(rc.getWeight())
                        .build())
                .collect(Collectors.toList());

        // 5. Gán danh sách vào response chính
        response.setCriteriaList(criteriaResponses);

        return ApiResponse.success(response, "Lấy chi tiết đợt đánh giá thành công");
    }

    @Override
    public ApiResponse<AssessmentRoundResponse> createRound(CreateAssessmentRoundRequest dto) {
        InternshipPhase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Phase không tồn tại"));

        // Tạo Round
        AssessmentRound round = AssessmentRound.builder()
                .internshipPhase(phase)
                .roundName(dto.getRoundName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .description(dto.getDescription())
                .isActive(true)
                .build();

        AssessmentRound savedRound = roundRepository.save(round);

        // Lưu danh sách tiêu chí vào bảng round_criteria
        if (dto.getCriteria() != null) {
            List<RoundCriteria> roundCriteriaList = dto.getCriteria().stream().map(cReq -> {
                EvaluationCriteria criteria = criteriaRepository.findById(cReq.getCriterionId())
                        .orElseThrow(() -> new RuntimeException("Tiêu chí không tồn tại"));

                return RoundCriteria.builder()
                        .assessmentRound(savedRound)
                        .evaluationCriteria(criteria)
                        .weight(cReq.getWeight())
                        .build();
            }).collect(Collectors.toList());

            roundCriteriaRepository.saveAll(roundCriteriaList);
        }

        return ApiResponse.success(modelMapper.map(savedRound, AssessmentRoundResponse.class), "Tạo đợt đánh giá thành công");
    }

    @Override
    public ApiResponse<AssessmentRoundResponse> updateRound(Long id, CreateAssessmentRoundRequest dto) {
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đánh giá ID: " + id));

        // Cập nhật thông tin cơ bản
        round.setRoundName(dto.getRoundName());
        round.setStartDate(dto.getStartDate());
        round.setEndDate(dto.getEndDate());
        round.setDescription(dto.getDescription());

        // Nếu có danh sách tiêu chí mới, thực hiện cập nhật lại bảng trung gian
        if (dto.getCriteria() != null) {
            // Kiểm tra tổng trọng số phải bằng 1.0 (100%)
            BigDecimal totalWeight = dto.getCriteria().stream()
                    .map(RoundCriterionRequest::getWeight)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalWeight.compareTo(BigDecimal.ONE) != 0) {
                throw new RuntimeException("Tổng trọng số của các tiêu chí phải bằng 1.0 (100%)");
            }

            // Xóa các tiêu chí cũ của vòng này trước
            roundCriteriaRepository.deleteAllByAssessmentRound_RoundId(id);

            // Thêm mới danh sách tiêu chí từ Request
            List<RoundCriteria> newCriteria = dto.getCriteria().stream().map(cReq -> {
                EvaluationCriteria ec = criteriaRepository.findById(cReq.getCriterionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tiêu chí ID " + cReq.getCriterionId() + " không tồn tại"));
                return RoundCriteria.builder()
                        .assessmentRound(round)
                        .evaluationCriteria(ec)
                        .weight(cReq.getWeight())
                        .build();
            }).collect(Collectors.toList());

            roundCriteriaRepository.saveAll(newCriteria);
        }

        AssessmentRound updated = roundRepository.save(round);
        return ApiResponse.success(modelMapper.map(updated, AssessmentRoundResponse.class), "Cập nhật đợt đánh giá thành công");
    }

    @Override
    public ApiResponse<Void> deleteRound(Long id) {
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đánh giá ID: " + id));

        // RÀNG BUỘC: Nếu đã có điểm (AssessmentResult) thì tuyệt đối không cho xóa
        boolean hasResults = resultRepository.existsByAssessmentRound_RoundId(id);
        if (hasResults) {
            throw new RuntimeException("Không thể xóa đợt đánh giá này vì đã có sinh viên được chấm điểm!");
        }

        // Xóa các tiêu chí liên kết trước (RoundCriteria)
        roundCriteriaRepository.deleteAllByAssessmentRound_RoundId(id);

        // Xóa vòng đánh giá
        roundRepository.delete(round);

        return ApiResponse.success(null, "Xóa đợt đánh giá thành công");
    }

    @Override
    public ApiResponse<List<RoundCriteriaResponse>> getCriteriaByRound(Long roundId) {
        if (!roundRepository.existsById(roundId)) {
            throw new RuntimeException("Không tìm thấy đợt đánh giá ID: " + roundId);
        }

        List<RoundCriteria> list = roundCriteriaRepository.findByAssessmentRound_RoundId(roundId);
        List<RoundCriteriaResponse> responses = list.stream()
                .map(rc -> RoundCriteriaResponse.builder()
                        .criterionId(rc.getEvaluationCriteria().getCriterionId())
                        .criterionName(rc.getEvaluationCriteria().getCriterionName())
                        .maxScore(rc.getEvaluationCriteria().getMaxScore())
                        .weight(rc.getWeight())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách tiêu chí của vòng thành công");
    }

    @Override
    public ApiResponse<RoundCriteriaResponse> getCriterionInRound(Long roundId, Long criterionId) {
        RoundCriteria rc = roundCriteriaRepository.findByAssessmentRound_RoundIdAndEvaluationCriteria_CriterionId(roundId, criterionId)
                .orElseThrow(() -> new RuntimeException("Tiêu chí này không tồn tại trong đợt đánh giá này"));

        RoundCriteriaResponse response = RoundCriteriaResponse.builder()
                .criterionId(rc.getEvaluationCriteria().getCriterionId())
                .criterionName(rc.getEvaluationCriteria().getCriterionName())
                .maxScore(rc.getEvaluationCriteria().getMaxScore())
                .weight(rc.getWeight())
                .build();

        return ApiResponse.success(response, "Thành công");
    }

    @Override
    public ApiResponse<Void> addCriterionToRound(Long roundId, RoundCriterionRequest dto) {
        // Kiểm tra xem đã tồn tại chưa để tránh lỗi UniqueConstraint
        if (roundCriteriaRepository.existsByAssessmentRound_RoundIdAndEvaluationCriteria_CriterionId(roundId, dto.getCriterionId())) {
            throw new RuntimeException("Tiêu chí này đã có trong đợt đánh giá!");
        }

        AssessmentRound round = roundRepository.findById(roundId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đánh giá"));
        EvaluationCriteria criteria = criteriaRepository.findById(dto.getCriterionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiêu chí"));

        RoundCriteria roundCriteria = RoundCriteria.builder()
                .assessmentRound(round)
                .evaluationCriteria(criteria)
                .weight(dto.getWeight())
                .build();

        roundCriteriaRepository.save(roundCriteria);
        return ApiResponse.success(null, "Thêm tiêu chí vào vòng thành công");
    }

    @Override
    public ApiResponse<Void> updateCriterionWeight(Long roundId, Long criterionId, UpdateWeightRequest dto) {
        RoundCriteria rc = roundCriteriaRepository.findByAssessmentRound_RoundIdAndEvaluationCriteria_CriterionId(roundId, criterionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiêu chí trong vòng này"));

        rc.setWeight(dto.getWeight());
        roundCriteriaRepository.save(rc);
        return ApiResponse.success(null, "Cập nhật trọng số thành công");
    }

    @Override
    public ApiResponse<Void> removeCriterionFromRound(Long roundId, Long criterionId) {
        RoundCriteria rc = roundCriteriaRepository.findByAssessmentRound_RoundIdAndEvaluationCriteria_CriterionId(roundId, criterionId)
                .orElseThrow(() -> new RuntimeException("Tiêu chí không tồn tại trong vòng này"));

        // Lưu ý: Cần kiểm tra xem đã có điểm số (AssessmentResult) liên quan chưa trước khi xóa
        roundCriteriaRepository.delete(rc);
        return ApiResponse.success(null, "Xóa tiêu chí khỏi vòng thành công");
    }
}
