package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import com.jpmt.powerlab.models.mappers.WorkoutSessionMapper;
import com.jpmt.powerlab.repositories.TrainingSessionTemplateRepository;
import com.jpmt.powerlab.repositories.WorkoutSessionRepository;
import com.jpmt.powerlab.repositories.WorkoutSetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {
    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private WorkoutSessionMapper workoutSessionMapper;

    @Mock
    private TrainingSessionTemplateRepository templateRepository;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @Test
    void saveWorkoutSessionWithOutTemplate() {
        WorkoutSessionRequest sessionRequest = new WorkoutSessionRequest(LocalDate.now(), null);
        WorkoutSession workoutSession = WorkoutSession.builder()
                .id(1L)
                .date(LocalDate.now())
                .build();

        WorkoutSessionDetailResponse expectedResponse = new WorkoutSessionDetailResponse(
                1L,
                LocalDate.now(),
                null,
                java.util.Collections.emptyList()
        );

        when(workoutSessionMapper.toEntity(sessionRequest)).thenReturn(workoutSession);
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenReturn(workoutSession);
        when(workoutSessionMapper.toDetailResponse(workoutSession)).thenReturn(expectedResponse);

        WorkoutSessionDetailResponse result = workoutService.saveWorkoutSession(sessionRequest);

        assertNotNull(result);
        assertEquals(sessionRequest.date(), result.date());
    }

    @Test
    void saveWorkoutSessionWithTemplate() {
        WorkoutSessionRequest sessionRequest = new WorkoutSessionRequest(LocalDate.now(), 1L);
        WorkoutSession workoutSession = WorkoutSession.builder()
                .id(1L)
                .date(LocalDate.now())
                .build();

        WorkoutSessionDetailResponse expectedResponse = new WorkoutSessionDetailResponse(
                1L,
                LocalDate.now(),
                null,
                java.util.Collections.emptyList()
        );

        when(workoutSessionMapper.toEntity(sessionRequest)).thenReturn(workoutSession);
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenReturn(workoutSession);
        when(workoutSessionMapper.toDetailResponse(workoutSession)).thenReturn(expectedResponse);
        when(templateRepository.findById(1L)).thenReturn(java.util.Optional.of(new TrainingSessionTemplate()));

        WorkoutSessionDetailResponse result = workoutService.saveWorkoutSession(sessionRequest);

        assertNotNull(result);
        assertEquals(sessionRequest.date(), result.date());

    }

    @Test
    void createWorkoutFromTemplate() {
        TrainingSessionTemplate template = new TrainingSessionTemplate();
        template.setId(1L);

        WorkoutSessionRequest sessionRequest = new WorkoutSessionRequest(LocalDate.now(), 1L);
        WorkoutSession workoutSession = WorkoutSession.builder()
                .id(1L)
                .date(LocalDate.now())
                .trainingSessionTemplate(template)
                .build();
        WorkoutSessionDetailResponse expectedResponse = new WorkoutSessionDetailResponse(
                1L,
                LocalDate.now(),
                1L,
                java.util.Collections.emptyList()
        );
        when(templateRepository.findById(1L)).thenReturn(java.util.Optional.of(template));
        when(workoutSessionMapper.toEntity(sessionRequest)).thenReturn(workoutSession);
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenReturn(workoutSession);
        when(workoutSessionMapper.toDetailResponse(workoutSession)).thenReturn(expectedResponse);

        WorkoutSessionDetailResponse result = workoutService.createWorkoutFromTemplate(1L, sessionRequest);

        assertNotNull(result);
        assertEquals(sessionRequest.date(), result.date());
    }

    @Test
    void findAllWorkoutSessionsByDateRange() {
        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        List<WorkoutSession> sessions = List.of(
                WorkoutSession.builder().id(1L).date(LocalDate.now()).build(),
                WorkoutSession.builder().id(2L).date(LocalDate.now().minusDays(10)).build()
        );
        when(workoutSessionMapper.toSummaryResponse(sessions.get(0)))
                .thenReturn(new WorkoutSessionSummaryResponse(1L, LocalDate.now(), 1L, java.util.Collections.emptyList()));
        when(workoutSessionMapper.toSummaryResponse(sessions.get(1)))
                .thenReturn(new WorkoutSessionSummaryResponse(2L, LocalDate.now().minusDays(10), 1L, java.util.Collections.emptyList()));

        when(workoutSessionRepository.findFullByDateRange(from, to)).thenReturn(sessions);
        List<WorkoutSessionSummaryResponse> result = workoutService.findAllWorkoutSessionsByDateRange(from, to);

        assertEquals(2, result.size());
        assertEquals(LocalDate.now(), result.getFirst().date());
    }

    @Test
    void findFullDayByIdWithoutEntries() {
        List<TrainingSetTemplate> setTemplate = Collections.singletonList(TrainingSetTemplate.builder().id(1L).targetSets(5).targetReps(12).targetWeight(100.0).targetIntensity(80).targetIntensityType(IntensityType.PERCENTAGE).rest("2 min").notes("set notes").displayOrder(1).build());
        TrainingSessionTemplate template = TrainingSessionTemplate.builder().id(1L).blockName("block name").weekNumber(1).dayInWeek(1).conjugatedDayType(ConjugatedDayType.ME_LOWER).notes("notess").setTemplates(setTemplate).build();
        List<WorkoutEntry> entries = Collections.emptyList();
        WorkoutSession workoutSession = WorkoutSession.builder().id(1L).date(LocalDate.now()).trainingSessionTemplate(template).entries(entries).build();

        WorkoutDayResponse.TemplateInfo templateInfo = new WorkoutDayResponse.TemplateInfo(
                1L,
                "block name",
                1,
                1,
                "ME_LOWER",
                "notess",
                Collections.emptyList()
        );

        WorkoutDayResponse expectedResponse = new WorkoutDayResponse(
                1L,
                LocalDate.now(),
                templateInfo,
                Collections.emptyList()
        );

        when(workoutSessionRepository.findHeaderById(1L)).thenReturn(java.util.Optional.of(workoutSession));
        when(templateRepository.findById(1L)).thenReturn(java.util.Optional.of(template));
        when(workoutSessionMapper.toDayResponse(workoutSession)).thenReturn(expectedResponse);

        WorkoutDayResponse result = workoutService.findFullDayById(1L);
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.date());
        assertEquals(1L, result.workoutId());
        assertEquals("block name", result.template().blockName());
    }

    @Test
    void findFullDayById() {
        List<TrainingSetTemplate> setTemplate = Collections.singletonList( TrainingSetTemplate.builder().id(1L).targetSets(5).targetReps(12).targetWeight(100.0).targetIntensity(80).targetIntensityType(IntensityType.PERCENTAGE).rest("2 min").notes("set notes").displayOrder(1).build());
        TrainingSessionTemplate template =  TrainingSessionTemplate.builder().id(1L).blockName("block name").weekNumber(1).dayInWeek(1).conjugatedDayType(ConjugatedDayType.ME_LOWER).notes("notess").setTemplates(setTemplate).build();
        List<WorkoutEntry> entries = Collections.singletonList( WorkoutEntry.builder().id(1L).actualSets(5).actualReps(12).actualPlateWeight(100.0).actualRirOrRpe("80").isWarmup(true).notes("entry notes").build());
        WorkoutSession workoutSession = WorkoutSession.builder().id(1L).date(LocalDate.now()).trainingSessionTemplate(template).entries(entries).build();

        WorkoutDayResponse.TemplateInfo templateInfo = new WorkoutDayResponse.TemplateInfo(
                1L,
                "block name",
                1,
                1,
                "ME_LOWER",
                "notess",
                Collections.emptyList()
        );
        ExerciseResponse exerciseResponse = new ExerciseResponse(
                1L,
                "exercise name",
                MainPattern.SQUAT,
                Implement.BARBELL,
                Setup.STANDARD,
                Stance.MEDIUM,
                Grip.PRONATED,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        List<WorkoutDayResponse.Entry> entry = Collections.singletonList(new WorkoutDayResponse.Entry(
                1L,
                exerciseResponse,
                new WorkoutDayResponse.Target(5, 12, 100.0, "PERCENTAGE", "80", "2 min"),
                "entry notes",
                true,
                Collections.emptyList()
        ));
        WorkoutDayResponse expectedResponse = new WorkoutDayResponse(
                1L,
                LocalDate.now(),
                templateInfo,
                entry
        );

        when(workoutSessionRepository.findHeaderById(1L)).thenReturn(java.util.Optional.of(workoutSession));
        when(templateRepository.findById(1L)).thenReturn(java.util.Optional.of(template));
        when(workoutSetRepository.findByWorkoutEntryIdIn(List.of(1L))).thenReturn(Collections.emptyList());
        when(workoutSessionMapper.toDayResponse(workoutSession)).thenReturn(expectedResponse);

        WorkoutDayResponse result = workoutService.findFullDayById(1L);
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.date());
        assertEquals(1L, result.workoutId());
        assertEquals("block name", result.template().blockName());
    }

    @Test
    void findFullDayByIdThrowsWhenTemplateMissing() {
        List<TrainingSetTemplate> setTemplate = Collections.singletonList( TrainingSetTemplate.builder().id(1L).targetSets(5).targetReps(12).targetWeight(100.0).targetIntensity(80).targetIntensityType(IntensityType.PERCENTAGE).rest("2 min").notes("set notes").displayOrder(1).build());
        TrainingSessionTemplate template = TrainingSessionTemplate.builder().id(1L).blockName("block name").weekNumber(1).dayInWeek(1).conjugatedDayType(ConjugatedDayType.ME_LOWER).notes("notess").setTemplates(setTemplate).build();
        List<WorkoutEntry> entries = Collections.emptyList();
        WorkoutSession workoutSession = WorkoutSession.builder().id(1L).date(LocalDate.now()).trainingSessionTemplate(template).entries(entries).build();

        when(workoutSessionRepository.findHeaderById(1L)).thenReturn(java.util.Optional.of(workoutSession));
        when(templateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutService.findFullDayById(1L));
    }
}
