package com.jpmt.powerlab.models.mappers;

import com.jpmt.powerlab.models.domain.TrainingSessionTemplate;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateDetailResponse;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateRequest;
import com.jpmt.powerlab.models.dto.trainingsessiontemplate.TrainingSessionTemplateSummaryResponse;
import com.jpmt.powerlab.services.PrescribedWeightResolver.ResolvedPrescription;
import org.mapstruct.*;

import java.util.Map;

@Mapper(componentModel = "spring", uses = TrainingSetTemplateMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrainingSessionTemplateMapper  {

    @Mapping(source = "conjugatedDayType", target = "conjugatedDayType")
    @Mapping(source = "trainingSets", target = "setTemplates", qualifiedByName = "toResponseWithResolvedWeights")
    public abstract TrainingSessionTemplateDetailResponse toDetailResponse(TrainingSessionTemplate entity, @Context Map<Long, ResolvedPrescription> resolved);

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
