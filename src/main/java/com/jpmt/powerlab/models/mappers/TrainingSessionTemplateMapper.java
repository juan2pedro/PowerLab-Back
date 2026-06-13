package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.TrainingSessionTemplate;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateDetailResponse;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateRequest;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = TrainingSetTemplateMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TrainingSessionTemplateMapper {

    @Mapping(source = "conjugatedDayType", target = "conjugatedDayType")
    @Mapping(target = "setTemplates", ignore = true)
    public abstract TrainingSessionTemplateDetailResponse toDetailResponse(TrainingSessionTemplate entity);

    public abstract TrainingSessionTemplateSummaryResponse toSummaryResponse(TrainingSessionTemplate entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingSets", ignore = true)
    public abstract TrainingSessionTemplate toEntity(TrainingSessionTemplateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingSets", ignore = true)
    public abstract void updateEntityFromRequest(
            TrainingSessionTemplateRequest request,
            @MappingTarget TrainingSessionTemplate entity
    );
}
