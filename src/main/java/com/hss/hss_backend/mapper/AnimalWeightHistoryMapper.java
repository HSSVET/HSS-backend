package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.AnimalWeightHistoryRequest;
import com.hss.hss_backend.dto.response.AnimalWeightHistoryResponse;
import com.hss.hss_backend.entity.AnimalWeightHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AnimalWeightHistoryMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "animal", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  AnimalWeightHistory toEntity(AnimalWeightHistoryRequest request);

  @Mapping(target = "animalId", source = "animal.animalId")
  AnimalWeightHistoryResponse toResponse(AnimalWeightHistory entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "animal", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void updateEntity(@MappingTarget AnimalWeightHistory entity, AnimalWeightHistoryRequest request);
}
