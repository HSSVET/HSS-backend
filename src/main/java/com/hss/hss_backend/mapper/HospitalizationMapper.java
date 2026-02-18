package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.HospitalizationDto;
import com.hss.hss_backend.dto.HospitalizationLogDto;
import com.hss.hss_backend.entity.Hospitalization;
import com.hss.hss_backend.entity.HospitalizationLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HospitalizationMapper {

  @Mapping(target = "animalId", source = "animal.animalId")
  @Mapping(target = "animalName", source = "animal.name")
  HospitalizationDto toDto(Hospitalization hospitalization);

  @Mapping(source = "animalId", target = "animal.animalId")
  Hospitalization toEntity(HospitalizationDto hospitalizationDto);

  List<HospitalizationDto> toDtoList(List<Hospitalization> hospitalizations);

  @Mapping(target = "hospitalizationId", source = "hospitalization.hospitalizationId")
  HospitalizationLogDto toLogDto(HospitalizationLog hospitalizationLog);

  @Mapping(source = "hospitalizationId", target = "hospitalization.hospitalizationId")
  HospitalizationLog toLogEntity(HospitalizationLogDto hospitalizationLogDto);
}
