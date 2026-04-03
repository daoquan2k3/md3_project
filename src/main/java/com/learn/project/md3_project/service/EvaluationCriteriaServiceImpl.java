package com.learn.project.md3_project.service;

import com.learn.project.md3_project.dto.request.EvaluationCriteriaRequest;
import com.learn.project.md3_project.dto.response.ApiResponse;
import com.learn.project.md3_project.dto.response.EvaluationCriteriaResponse;
import com.learn.project.md3_project.entity.EvaluationCriteria;
import com.learn.project.md3_project.exception.ResourceNotFoundException;
import com.learn.project.md3_project.repository.IEvaluationCriteriaRepository;
import com.learn.project.md3_project.repository.IRoundCriteriaRepository;
import com.learn.project.md3_project.service.impl.IEvaluationCriteriaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EvaluationCriteriaServiceImpl implements IEvaluationCriteriaService {
    private final IEvaluationCriteriaRepository criteriaRepository;
    private final IRoundCriteriaRepository roundCriteriaRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<List<EvaluationCriteriaResponse>> getAllCriteria() {
        List<EvaluationCriteria> list = criteriaRepository.findAll();
        List<EvaluationCriteriaResponse> responses = list.stream()
                .map(c -> modelMapper.map(c, EvaluationCriteriaResponse.class))
                .collect(Collectors.toList());
        return ApiResponse.success(responses, "Lấy danh sách tiêu chí thành công");
    }

    @Override
    public ApiResponse<EvaluationCriteriaResponse> getCriteriaDetail(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí ID: " + id));
        return ApiResponse.success(modelMapper.map(criteria, EvaluationCriteriaResponse.class), "Thành công");
    }

    @Override
    public ApiResponse<EvaluationCriteriaResponse> createCriteria(EvaluationCriteriaRequest dto) {
        if (criteriaRepository.existsByCriterionName(dto.getCriterionName())) {
            throw new RuntimeException("Tên tiêu chí này đã tồn tại!");
        }
        EvaluationCriteria criteria = modelMapper.map(dto, EvaluationCriteria.class);
        EvaluationCriteria saved = criteriaRepository.save(criteria);
        return ApiResponse.success(modelMapper.map(saved, EvaluationCriteriaResponse.class), "Tạo tiêu chí thành công");
    }

    @Override
    public ApiResponse<EvaluationCriteriaResponse> updateCriteria(Long id, EvaluationCriteriaRequest dto) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí ID: " + id));

        criteria.setCriterionName(dto.getCriterionName());
        criteria.setDescription(dto.getDescription());
        criteria.setMaxScore(dto.getMaxScore());

        EvaluationCriteria updated = criteriaRepository.save(criteria);
        return ApiResponse.success(modelMapper.map(updated, EvaluationCriteriaResponse.class), "Cập nhật thành công");
    }

    @Override
    public ApiResponse<Void> deleteCriteria(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiêu chí ID: " + id));

        boolean isUsed = roundCriteriaRepository.existsByEvaluationCriteria_CriterionId(id);
        if (isUsed) {
            throw new RuntimeException("Không thể xóa tiêu chí này vì nó đang được sử dụng trong các vòng đánh giá!");
        }

        criteriaRepository.delete(criteria);
        return ApiResponse.success(null, "Xóa tiêu chí thành công");
    }
}
