package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.*;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutDayResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionDetailResponse;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionRequest;
import com.jpmt.powerlab.models.dto.workoutsession.WorkoutSessionSummaryResponse;
import com.jpmt.powerlab.models.mappers.WorkoutEntryMapper;
import com.jpmt.powerlab.models.mappers.WorkoutSessionMapper;
import com.jpmt.powerlab.models.mappers.WorkoutSetMapper;
import com.jpmt.powerlab.repositories.*;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;
    @Mock
    private WorkoutEntryRepository entryRepository;
    @Mock
    private TrainingSessionTemplateRepository templateRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private WorkoutSetRepository workoutSetRepository;
    @Mock
    private WorkoutSessionMapper workoutSessionMapper;
    @Mock
    private WorkoutEntryMapper entryMapper;
    @Mock
    private WorkoutSetMapper workoutSetMapper;
    @Mock
    private PrescribedWeightResolver prescribedWeightResolver;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @Test
    void saveWorkoutSessionWithOutTemplate() {
        WorkoutSessionRequest sessionRequest = new WorkoutSessionRequest(LocalDate.now(), null,Status.COMPLETED);
        WorkoutSession workoutSession = WorkoutSession.builder()
                .id(1L)
                .date(LocalDate.now())
                .build();

        WorkoutSessionDetailResponse expectedResponse = new WorkoutSessionDetailResponse(
                1L,
                LocalDate.now(),
                null,
                Status.IN_PROGRESS,
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
        WorkoutSessionRequest sessionRequest = new WorkoutSessionRequest(LocalDate.now(), 1L,Status.COMPLETED);
        WorkoutSession workoutSession = WorkoutSession.builder()
                .id(1L)
                .date(LocalDate.now())
                .build();

        WorkoutSessionDetailResponse expectedResponse = new WorkoutSessionDetailResponse(
                1L,
                LocalDate.now(),
                null,
                Status.IN_PROGRESS,
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

        WorkoutSessionRequest sessionRequest = new WorkoutSessionRequest(LocalDate.now(), 1L,Status.COMPLETED);
        WorkoutSession workoutSession = WorkoutSession.builder()
                .id(1L)
                .date(LocalDate.now())
                .trainingSessionTemplate(template)
                .build();
        WorkoutSessionDetailResponse expectedResponse = new WorkoutSessionDetailResponse(
                1L,
                LocalDate.now(),
                1L,
                Status.IN_PROGRESS,
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
                .thenReturn(new WorkoutSessionSummaryResponse(1L, LocalDate.now(), 1L, Status.IN_PROGRESS, java.util.Collections.emptyList()));
        when(workoutSessionMapper.toSummaryResponse(sessions.get(1)))
                .thenReturn(new WorkoutSessionSummaryResponse(2L, LocalDate.now().minusDays(10), 1L, Status.IN_PROGRESS, java.util.Collections.emptyList()));

        when(workoutSessionRepository.findFullByDateRange(from, to)).thenReturn(sessions);
        List<WorkoutSessionSummaryResponse> result = workoutService.findAllWorkoutSessionsByDateRange(from, to);

        assertEquals(2, result.size());
        assertEquals(LocalDate.now(), result.getFirst().date());
    }

    @Test
    void findFullDayByDateWithoutEntries() {
        List<TrainingSetTemplate> setTemplate = Collections.singletonList(TrainingSetTemplate.builder().id(1L).targetSets(5).targetRepsMin(12).targetWeight(100.0).targetIntensityMax(80.00).targetIntensityType(IntensityType.PERCENTAGE_1RM).rest("2 min").notes("set notes").displayOrder(1).build());
        TrainingSessionTemplate template = TrainingSessionTemplate.builder().id(1L).blockName("block name").weekNumber(1).dayIndex(1).conjugatedDayType(ConjugatedDayType.ME_LOWER).notes("notess").trainingSets(setTemplate).build();
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
                Status.IN_PROGRESS,
                templateInfo,
                Collections.emptyList()
        );

        when(workoutSessionRepository.findFullByDate(LocalDate.now())).thenReturn(Collections.singletonList(workoutSession));
        when(templateRepository.findByIdOrderByDisplayOrder(1L)).thenReturn(Optional.of(template));
        when(workoutSessionMapper.toDayResponse(eq(workoutSession), any())).thenReturn(expectedResponse);

       Optional < WorkoutDayResponse > found = workoutService.findFullDayByDate(LocalDate.now());
       WorkoutDayResponse result = found.get();
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.date());
        assertEquals(1L, result.workoutId());
        assertEquals("block name", result.template().blockName());
    }

    @Test
    void findFullDayByDate() {
        List<TrainingSetTemplate> setTemplate = Collections.singletonList(TrainingSetTemplate.builder().id(1L).targetSets(5).targetRepsMin(12).targetWeight(100.0).targetIntensityMax(80.00).targetIntensityType(IntensityType.PERCENTAGE_1RM).rest("2 min").notes("set notes").displayOrder(1).build());
        TrainingSessionTemplate template = TrainingSessionTemplate.builder().id(1L).blockName("block name").weekNumber(1).dayIndex(1).conjugatedDayType(ConjugatedDayType.ME_LOWER).notes("notess").trainingSets(setTemplate).build();
        List<WorkoutEntry> entries = Collections.singletonList(WorkoutEntry.builder().id(1L).notes("entry notes").build());
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
                null
        );
        List<WorkoutDayResponse.WorkoutDayEntry> entry = Collections.singletonList(new WorkoutDayResponse.WorkoutDayEntry(
                1L,
                1,
                exerciseResponse,
                new WorkoutDayResponse.WorkoutDayTarget(5, 12, 100.0, IntensityType.PERCENTAGE_1RM, 80.0, "2 min"),
                "entry notes",
                Collections.emptyList(),
                1L
        ));
        WorkoutDayResponse expectedResponse = new WorkoutDayResponse(
                1L,
                LocalDate.now(),
                Status.IN_PROGRESS,
                templateInfo,
                entry
        );

        when(workoutSessionRepository.findFullByDate(LocalDate.now())).thenReturn(Collections.singletonList(workoutSession));
        when(templateRepository.findByIdOrderByDisplayOrder(1L)).thenReturn(Optional.of(template));
        when(workoutSetRepository.findByWorkoutEntryIdIn(List.of(1L))).thenReturn(Collections.emptyList());
        when(workoutSessionMapper.toDayResponse(eq(workoutSession), any())).thenReturn(expectedResponse);

        Optional<WorkoutDayResponse> found = workoutService.findFullDayByDate(LocalDate.now());
        assertTrue(found.isPresent());
        WorkoutDayResponse result = found.get();
        assertEquals(LocalDate.now(), result.date());
        assertEquals(1L, result.workoutId());
        assertEquals("block name", result.template().blockName());
    }

    @Test
    void findFullDayByDateReturnsEmptyWhenTemplateMissing() {
        List<TrainingSetTemplate> setTemplate = Collections.singletonList(TrainingSetTemplate.builder().id(1L).targetSets(5).targetRepsMin(12).targetWeight(100.0).targetIntensityMax(80.00).targetIntensityType(IntensityType.PERCENTAGE_1RM).rest("2 min").notes("set notes").displayOrder(1).build());
        TrainingSessionTemplate template = TrainingSessionTemplate.builder().id(1L).blockName("block name").weekNumber(1).dayIndex(1).conjugatedDayType(ConjugatedDayType.ME_LOWER).notes("notess").trainingSets(setTemplate).build();
        List<WorkoutEntry> entries = Collections.emptyList();
        LocalDate today = LocalDate.now();
        WorkoutSession workoutSession = WorkoutSession.builder().id(1L).date(today).trainingSessionTemplate(template).entries(entries).build();

        when(workoutSessionRepository.findFullByDate(today)).thenReturn(Collections.singletonList(workoutSession));
        when(templateRepository.findByIdOrderByDisplayOrder(1L)).thenReturn(Optional.empty());

        Optional<WorkoutDayResponse> found = workoutService.findFullDayByDate(today);
        assertTrue(found.isEmpty());
    }
}
