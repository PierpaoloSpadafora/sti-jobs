package unical.demacs.rdm.service;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.service.implementation.ScheduleServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private MachineTypeRepository machineTypeRepository;

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private Schedule schedule;
    private ScheduleDTO scheduleDTO;
    private Job job;
    private MachineType machineType;
    private static final Long TEST_ID = 1L;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setId(TEST_ID);

        machineType = MachineType.buildMachineType()
                .id(TEST_ID)
                .build();

        schedule = Schedule.scheduleBuilder()
                .id(TEST_ID)
                .job(job)
                .machineType(machineType)
                .startTime(NOW)
                .dueDate(NOW.plusDays(1))
                .duration(60L)
                .status(ScheduleStatus.PENDING)
                .build();

        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(TEST_ID);
        scheduleDTO.setJobId(TEST_ID);
        scheduleDTO.setMachineTypeId(TEST_ID);
        scheduleDTO.setStartTime(NOW);
        scheduleDTO.setDueDate(NOW.plusDays(1));
        scheduleDTO.setDuration(60L);
    }

    @Test
    void createSchedule_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(jobRepository.findById(TEST_ID)).thenReturn(Optional.of(job));
        when(machineTypeRepository.findById(TEST_ID)).thenReturn(Optional.of(machineType));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        Schedule result = scheduleService.createSchedule(scheduleDTO);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void createSchedule_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(RuntimeException.class, () -> scheduleService.createSchedule(scheduleDTO));
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void createSchedule_JobNotFound() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(jobRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> scheduleService.createSchedule(scheduleDTO));
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void getScheduleById_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findById(TEST_ID)).thenReturn(Optional.of(schedule));

        Optional<Schedule> result = scheduleService.getScheduleById(TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(TEST_ID, result.get().getId());
    }

    @Test
    void getAllSchedules_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getAllSchedules();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void updateSchedule_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findById(TEST_ID)).thenReturn(Optional.of(schedule));
        when(jobRepository.findById(TEST_ID)).thenReturn(Optional.of(job));
        when(machineTypeRepository.findById(TEST_ID)).thenReturn(Optional.of(machineType));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        Schedule result = scheduleService.updateSchedule(TEST_ID, scheduleDTO);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void deleteSchedule_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findById(TEST_ID)).thenReturn(Optional.of(schedule));

        boolean result = scheduleService.deleteSchedule(TEST_ID);

        assertTrue(result);
        verify(scheduleRepository).delete(schedule);
    }

    @Test
    void getSchedulesByStatus_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getSchedulesByStatus(ScheduleStatus.PENDING);

        assertFalse(result.isEmpty());
        assertEquals(ScheduleStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void updateScheduleStatus_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findById(TEST_ID)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        Schedule result = scheduleService.updateScheduleStatus(TEST_ID, ScheduleStatus.COMPLETED);

        assertNotNull(result);
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void getSchedulesInTimeRange_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getSchedulesInTimeRange(
                NOW.minusHours(1),
                NOW.plusHours(1)
        );

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void isTimeSlotAvailable_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        boolean result = scheduleService.isTimeSlotAvailable(
                machineType.getName(),
                NOW.plusDays(2),
                NOW.plusDays(2).plusHours(1)
        );

        assertTrue(result);
    }

    @Test
    void getUpcomingSchedules_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getUpcomingSchedules(NOW.minusHours(1));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getPastSchedules_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getPastSchedules(NOW.plusHours(1));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getSchedulesDueBefore_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findByDueDateBefore(any())).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getSchedulesDueBefore(NOW.plusDays(2));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getSchedulesDueAfter_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findByDueDateAfter(any())).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getSchedulesDueAfter(NOW.minusDays(1));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getSchedulesByMachineType_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getSchedulesByMachineType(machineType.getName());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getSchedulesByJobId_Success() {
        when(rateLimiter.tryAcquire()).thenReturn(true);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        List<Schedule> result = scheduleService.getSchedulesByJobId(TEST_ID);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
