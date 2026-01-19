package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.request.VaccineCreateRequest;
import com.hss.hss_backend.dto.request.VaccineUpdateRequest;
import com.hss.hss_backend.dto.response.VaccineResponse;
import com.hss.hss_backend.entity.Vaccine;
import org.mapstruct.*;

import java.time.Duration;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VaccineMapper {
    
    @Mapping(target = "vaccineId", ignore = true)
    @Mapping(target = "protectionPeriod", source = "protectionPeriodDays", qualifiedByName = "daysToDuration")
    @Mapping(target = "vaccinationRecords", ignore = true)
    Vaccine toEntity(VaccineCreateRequest request);
    
    @Mapping(target = "id", source = "vaccineId")
    @Mapping(target = "name", source = "vaccineName")
    @Mapping(target = "protectionPeriodDays", source = "protectionPeriod", qualifiedByName = "durationToDays")
    @Mapping(target = "manufacturer", constant = "")
    VaccineResponse toResponse(Vaccine vaccine);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "vaccineId", ignore = true)
    @Mapping(target = "protectionPeriod", source = "protectionPeriodDays", qualifiedByName = "daysToDuration")
    @Mapping(target = "vaccinationRecords", ignore = true)
    void updateEntityFromRequest(VaccineUpdateRequest request, @MappingTarget Vaccine vaccine);
    
    @Named("daysToDuration")
    default Duration daysToDuration(Long days) {
        return days != null ? Duration.ofDays(days) : null;
    }
    
    @Named("durationToDays")
    default Long durationToDays(Duration duration) {
        return duration != null ? duration.toDays() : null;
    }
}
