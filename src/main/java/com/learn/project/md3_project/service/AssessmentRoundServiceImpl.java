package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateAssessmentRoundRequest;
import com.learn.project.md3_project.dto.request.CreateRoundCriterionRequest;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentRoundServiceImpl implements IAssessmentRoundService {
    private final IAssessmentRoundRepository roundRepository;
    private final IInternshipPhaseRepository phaseRepository;
    private final IEvaluationCriteriaRepository criteriaRepository;
    private final IRoundCriteriaRepository roundCriteriaRepository;
    private final IAssessmentResultRepository resultRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<List<AssessmentRoundResponse>> getAllRounds(Long phaseId) {
        List<AssessmentRound> rounds = (phaseId == null)
                ? roundRepository.findAll()
                : roundRepository.findByInternshipPhaseId(phaseId);

        List<AssessmentRoundResponse> responses = rounds.stream()
                .map(r -> modelMapper.map(r, AssessmentRoundResponse.class))
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách đợt đánh giá thành công");
    }

    @Override
    public ApiResponse<AssessmentRoundResponse> getRoundDetail(Long id) {
        // Tìm vòng đánh giá
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đánh giá ID: " + id));

        // Map thông tin cơ bản của Round
        AssessmentRoundResponse response = modelMapper.map(round, AssessmentRoundResponse.class);

        // Lấy danh sách tiêu chí đã gán cho vòng này
        List<RoundCriteria> roundCriteriaList = roundCriteriaRepository.findByRoundId(id);

        // Chuyển đổi sang List<RoundCriteriaResponse>
        List<RoundCriteriaResponse> criteriaResponses = roundCriteriaList.stream()
                .map(rc -> RoundCriteriaResponse.builder()
                        .criterionId(rc.getEvaluationCriteria().getCriterionId())
                        .criterionName(rc.getEvaluationCriteria().getCriterionName())
                        .maxScore(rc.getEvaluationCriteria().getMaxScore())
                        .weight(rc.getWeight())
                        .build())
                .collect(Collectors.toList());

        // Gán danh sách vào response chính
        response.setCriteriaList(criteriaResponses);

        return ApiResponse.success(response, "Lấy chi tiết đợt đánh giá thành công");
    }

    @Override
    @Transactional
    public ApiResponse<AssessmentRoundResponse> createRound(CreateAssessmentRoundRequest dto) {
        log.info("Bắt đầu tạo đợt đánh giá mới: {}", dto.getRoundName());

        InternshipPhase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Giai đoạn thực tập không tồn tại"));

        // Map thủ công từ DTO sang Entity AssessmentRound
        AssessmentRound round = AssessmentRound.builder()
                .internshipPhase(phase)
                .roundName(dto.getRoundName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .description(dto.getDescription())
                .isActive(true)
                .build();

        AssessmentRound savedRound = roundRepository.save(round);

        // Map thủ công
        if (dto.getCriteria() != null && !dto.getCriteria().isEmpty()) {
            List<RoundCriteria> roundCriteriaList = dto.getCriteria().stream()
                    .map(cReq -> {
                        EvaluationCriteria ec = criteriaRepository.findById(cReq.getCriterionId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí ID: " + cReq.getCriterionId()));

                        return RoundCriteria.builder()
                                .assessmentRound(savedRound)
                                .evaluationCriteria(ec)
                                .weight(cReq.getWeight())
                                .build();
                    })
                    .collect(Collectors.toList());

            roundCriteriaRepository.saveAll(roundCriteriaList);
            log.info("Đã lưu {} tiêu chí cho vòng {}", roundCriteriaList.size(), savedRound.getRoundName());
        }

        return ApiResponse.success(modelMapper.map(savedRound, AssessmentRoundResponse.class), "Tạo đợt đánh giá thành công");
    }

    @Override
    @Transactional
    public ApiResponse<AssessmentRoundResponse> updateRound(Long id, CreateAssessmentRoundRequest dto) {
        log.info("Cập nhật đợt đánh giá ID: {}", id);

        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đánh giá ID: " + id));

        round.setRoundName(dto.getRoundName());
        round.setStartDate(dto.getStartDate());
        round.setEndDate(dto.getEndDate());
        round.setDescription(dto.getDescription());

        // Xử lý logic tiêu chí
        if (dto.getCriteria() != null) {
            // Kiểm tra tổng trọng số (Phòng ngừa lỗi từ Frontend)
            BigDecimal totalWeight = dto.getCriteria().stream()
                    .map(CreateRoundCriterionRequest::getWeight)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalWeight.compareTo(BigDecimal.ONE) != 0) {
                throw new RuntimeException("Tổng trọng số của các tiêu chí phải bằng 1.0 (100%)");
            }

            // XÓA CŨ: Dọn dẹp bảng trung gian trước khi ghi mới
            roundCriteriaRepository.deleteAllByAssessmentRoundId(id);

            // GHI MỚI THỦ CÔNG
            List<RoundCriteria> newCriteria = dto.getCriteria().stream()
                    .map(cReq -> {
                        EvaluationCriteria ec = criteriaRepository.findById(cReq.getCriterionId())
                                .orElseThrow(() -> new ResourceNotFoundException("Tiêu chí không tồn tại ID: " + cReq.getCriterionId()));

                        return RoundCriteria.builder()
                                .assessmentRound(round)
                                .evaluationCriteria(ec)
                                .weight(cReq.getWeight())
                                .build();
                    })
                    .collect(Collectors.toList());

            roundCriteriaRepository.saveAll(newCriteria);
        }

        AssessmentRound updated = roundRepository.save(round);
        return ApiResponse.success(modelMapper.map(updated, AssessmentRoundResponse.class), "Cập nhật đợt đánh giá thành công");
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteRound(Long id) {
        AssessmentRound round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đánh giá ID: " + id));

        // Nếu đã có điểm (AssessmentResult) thì tuyệt đối không cho xóa
        boolean hasResults = resultRepository.existsByRoundId(id);
        if (hasResults) {
            throw new RuntimeException("Không thể xóa đợt đánh giá này vì đã có sinh viên được chấm điểm!");
        }

        // Xóa các tiêu chí liên kết trước (RoundCriteria)
        roundCriteriaRepository.deleteAllByAssessmentRoundId(id);

        // Xóa vòng đánh giá
        roundRepository.delete(round);

        return ApiResponse.success(null, "Xóa đợt đánh giá thành công");
    }
}
