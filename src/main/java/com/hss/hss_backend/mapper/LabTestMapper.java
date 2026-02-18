package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.response.LabResultResponse;
import com.hss.hss_backend.dto.response.LabTestResponse;
import com.hss.hss_backend.entity.LabResult;
import com.hss.hss_backend.entity.LabTest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LabTestMapper {

    public static LabTestResponse toResponse(LabTest labTest) {
        return LabTestResponse.builder()
                .testId(labTest.getTestId())
                .animalId(labTest.getAnimal().getAnimalId())
                .animalName(labTest.getAnimal().getName())
                .testName(labTest.getTestName())
                .date(labTest.getDate())
                .status(labTest.getStatus().name())
                .results(labTest.getLabResults() != null ? labTest.getLabResults().stream()
                        .map(LabTestMapper::toResultResponse)
                        .collect(Collectors.toList()) : null)
                .createdAt(labTest.getCreatedAt())
                .updatedAt(labTest.getUpdatedAt())
                .build();
    }

    public static LabResultResponse toResultResponse(LabResult labResult) {
        return LabResultResponse.builder()
                .resultId(labResult.getResultId())
                .testId(labResult.getLabTest().getTestId())
                .result(labResult.getResult())
                .value(labResult.getValue())
                .unit(labResult.getUnit())
                .normalRange(labResult.getNormalRange())
                .interpretation(labResult.getInterpretation())
                .fileUrl(labResult.getFileUrl())
                .createdAt(labResult.getCreatedAt())
                .updatedAt(labResult.getUpdatedAt())
                .build();
    }

    public static List<LabTestResponse> toResponseList(List<LabTest> labTests) {
        return labTests.stream()
                .map(LabTestMapper::toResponse)
                .collect(Collectors.toList());
    }
}
