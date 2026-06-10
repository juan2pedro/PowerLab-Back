package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.TrainingSessionTemplate;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateDetailResponse;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateRequest;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = TrainingSetTemplateMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrainingSessionTemplateMapper {
    @Mapping(source = "conjugatedDayType", target = "conjugatedDayType")
    TrainingSessionTemplateDetailResponse toDetailResponse(TrainingSessionTemplate entity);

    TrainingSessionTemplateSummaryResponse toSummaryResponse(TrainingSessionTemplate entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "templates", ignore = true) // MapStruct ve "templates" en el builder de Lombok para el campo `setTemplates`
    TrainingSessionTemplate toCreateEntity(TrainingSessionTemplateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "templates", ignore = true)
    TrainingSessionTemplate toEntity(TrainingSessionTemplateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "setTemplates", ignore = true) // por setter, MapStruct lo ve como "setTemplates"
    void updateEntityFromRequest(
            TrainingSessionTemplateRequest request,
            @MappingTarget TrainingSessionTemplate entity
    );
}
