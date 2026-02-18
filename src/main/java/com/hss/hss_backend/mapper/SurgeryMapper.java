package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.SurgeryDto;
import com.hss.hss_backend.dto.SurgeryMedicationDto;
import com.hss.hss_backend.entity.Surgery;
import com.hss.hss_backend.entity.SurgeryMedication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SurgeryMapper {

  @Mapping(target = "animalId", source = "animal.animalId")
  @Mapping(target = "animalName", source = "animal.name")
  SurgeryDto toDto(Surgery surgery);

  @Mapping(source = "animalId", target = "animal.animalId")
  Surgery toEntity(SurgeryDto surgeryDto);

  List<SurgeryDto> toDtoList(List<Surgery> surgeries);

  @Mapping(target = "surgeryId", source = "surgery.surgeryId")
  SurgeryMedicationDto toMedicationDto(SurgeryMedication surgeryMedication);

  @Mapping(source = "surgeryId", target = "surgery.surgeryId")
  SurgeryMedication toMedicationEntity(SurgeryMedicationDto surgeryMedicationDto);
}
