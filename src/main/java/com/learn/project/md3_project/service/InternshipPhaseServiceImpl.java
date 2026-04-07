package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.CreateInternshipPhaseRequest;
import com.learn.project.md3_project.dto.request.UpdateInternshipPhaseRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.AssessmentRoundResponse;
import com.learn.project.md3_project.dto.response.InternshipPhaseResponse;
import com.learn.project.md3_project.entity.AssessmentRound;
import com.learn.project.md3_project.entity.InternshipPhase;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IAssessmentRoundRepository;
import com.learn.project.md3_project.repository.IInternshipAssignmentRepository;
import com.learn.project.md3_project.repository.IInternshipPhaseRepository;
import com.learn.project.md3_project.service.impl.IInternshipPhaseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipPhaseServiceImpl implements IInternshipPhaseService {
    private final IInternshipPhaseRepository phaseRepository;
    private final IAssessmentRoundRepository roundRepository;
    private final IInternshipAssignmentRepository assignmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<List<InternshipPhaseResponse>> getAllPhases() {
        List<InternshipPhase> phases = phaseRepository.findAll();

        List<InternshipPhaseResponse> responses = phases.stream()
                .map(phase -> modelMapper.map(phase, InternshipPhaseResponse.class))
                .collect(Collectors.toList());

        return ApiResponse.success(responses, "Lấy danh sách giai đoạn thực tập thành công");
    }

    @Override
    public ApiResponse<InternshipPhaseResponse> getPhaseDetail(Long phaseId) {
        InternshipPhase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn thực tập ID: " + phaseId));

        InternshipPhaseResponse response = modelMapper.map(phase, InternshipPhaseResponse.class);

        List<AssessmentRound> rounds = roundRepository.findByInternshipPhaseId(phaseId);

        List<AssessmentRoundResponse> roundDtos = rounds.stream()
                .map(round -> modelMapper.map(round, AssessmentRoundResponse.class))
                .collect(Collectors.toList());

        response.setRounds(roundDtos);

        return ApiResponse.success(response, "Lấy chi tiết giai đoạn thực tập thành công");
    }

    @Override
    public ApiResponse<InternshipPhaseResponse> createPhase(CreateInternshipPhaseRequest dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc!");
        }

        if (phaseRepository.existsByPhaseName(dto.getPhaseName())) {
            throw new RuntimeException("Tên giai đoạn thực tập này đã tồn tại!");
        }

        InternshipPhase phase = InternshipPhase.builder()
                .phaseName(dto.getPhaseName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .description(dto.getDescription())
                .build();

        InternshipPhase savedPhase = phaseRepository.save(phase);

        InternshipPhaseResponse response = modelMapper.map(savedPhase, InternshipPhaseResponse.class);
        return ApiResponse.success(response, "Tạo giai đoạn thực tập mới thành công");
    }

    @Override
    public ApiResponse<InternshipPhaseResponse> updatePhase(Long phaseId, UpdateInternshipPhaseRequest dto) {
        InternshipPhase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn ID: " + phaseId));

        if (dto.getPhaseName() != null) phase.setPhaseName(dto.getPhaseName());
        if (dto.getDescription() != null) phase.setDescription(dto.getDescription());


        LocalDate start = (dto.getStartDate() != null) ? dto.getStartDate() : phase.getStartDate();
        LocalDate end = (dto.getEndDate() != null) ? dto.getEndDate() : phase.getEndDate();

        if (start.isAfter(end)) {
            throw new RuntimeException("Ngày bắt đầu không thể sau ngày kết thúc!");
        }

        phase.setStartDate(start);
        phase.setEndDate(end);

        InternshipPhase updated = phaseRepository.save(phase);
        return ApiResponse.success(modelMapper.map(updated, InternshipPhaseResponse.class), "Cập nhật thành công");
    }

    @Override
    public ApiResponse<Void> deletePhase(Long phaseId) {
        InternshipPhase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giai đoạn ID: " + phaseId));

        // KIỂM TRA RÀNG BUỘC: Nếu đã có sinh viên được phân công vào Phase này thì không cho xóa
        boolean hasAssignments = assignmentRepository.existsByInternshipPhaseId(phaseId);

        if (hasAssignments) {
            throw new RuntimeException("Không thể xóa giai đoạn này vì đã có dữ liệu phân công thực tập!");
        }

        phaseRepository.delete(phase);
        return ApiResponse.success(null, "Xóa giai đoạn thực tập thành công");
    }
}
