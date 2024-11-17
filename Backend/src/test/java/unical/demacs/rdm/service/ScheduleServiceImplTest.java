package unical.demacs.rdm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.service.implementation.ScheduleServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private JobRepository jobRepository;

    private ScheduleServiceImpl scheduleService;

    private ScheduleDTO scheduleDTO;
    private Schedule schedule;
    private Job job;

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setId(1L);

        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(1L);
        scheduleDTO.setJobId(job.getId());
        scheduleDTO.setMachineType("TypeA");
        scheduleDTO.setDueDate(LocalDateTime.now().plusDays(1));
        scheduleDTO.setStartTime(LocalDateTime.now());
        scheduleDTO.setDuration(120L); // Durata in minuti
        scheduleDTO.setStatus(ScheduleStatus.SCHEDULED.toString());

        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setJob(job);
        schedule.setMachineType(scheduleDTO.getMachineType());
        schedule.setDueDate(scheduleDTO.getDueDate());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setDuration(scheduleDTO.getDuration());
        schedule.setStatus(ScheduleStatus.SCHEDULED);

        scheduleService = new ScheduleServiceImpl(scheduleRepository, jobRepository);
    }

    @Test
    void testCreateSchedule_Success() {
        when(jobRepository.findById(eq(scheduleDTO.getJobId()))).thenReturn(Optional.of(job));
        when(scheduleRepository.findByMachineType(eq(scheduleDTO.getMachineType()))).thenReturn(new ArrayList<>());
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        ScheduleDTO createdSchedule = scheduleService.createSchedule(scheduleDTO);

        assertNotNull(createdSchedule);
        assertEquals(scheduleDTO.getJobId(), createdSchedule.getJobId());
        assertEquals(scheduleDTO.getMachineType(), createdSchedule.getMachineType());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void testCreateSchedule_InvalidTimeSlot() {
        when(jobRepository.findById(eq(scheduleDTO.getJobId()))).thenReturn(Optional.of(job));
        Schedule conflictingSchedule = new Schedule();
        conflictingSchedule.setStartTime(scheduleDTO.getStartTime().minusHours(1));
        conflictingSchedule.setDuration(240L); // 4 ore
        conflictingSchedule.setMachineType(scheduleDTO.getMachineType());
        when(scheduleRepository.findByMachineType(eq(scheduleDTO.getMachineType()))).thenReturn(List.of(conflictingSchedule));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleDTO));

        assertEquals("Time slot is not available for the selected machine type", exception.getMessage());
    }

    @Test
    void testGetScheduleById_Found() {
        when(scheduleRepository.findById(eq(schedule.getId()))).thenReturn(Optional.of(schedule));

        Optional<ScheduleDTO> foundSchedule = scheduleService.getScheduleById(schedule.getId());

        assertTrue(foundSchedule.isPresent());
        assertEquals(schedule.getId(), foundSchedule.get().getId());
    }

    @Test
    void testGetScheduleById_NotFound() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ScheduleDTO> foundSchedule = scheduleService.getScheduleById(1L);

        assertFalse(foundSchedule.isPresent());
    }

    @Test
    void testGetAllSchedules() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<ScheduleDTO> schedules = scheduleService.getAllSchedules();

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
    }

    @Test
    void testUpdateSchedule_Success() {
        when(scheduleRepository.findById(eq(schedule.getId()))).thenReturn(Optional.of(schedule));
        when(jobRepository.findById(eq(scheduleDTO.getJobId()))).thenReturn(Optional.of(job));
        when(scheduleRepository.findByMachineType(eq(scheduleDTO.getMachineType()))).thenReturn(new ArrayList<>());
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(schedule.getId(), scheduleDTO);

        assertNotNull(updatedSchedule);
        assertEquals(scheduleDTO.getJobId(), updatedSchedule.getJobId());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void testUpdateSchedule_NotFound() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> scheduleService.updateSchedule(1L, scheduleDTO));

        assertEquals("Schedule not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeleteSchedule_Success() {
        when(scheduleRepository.existsById(eq(schedule.getId()))).thenReturn(true);

        boolean result = scheduleService.deleteSchedule(schedule.getId());

        assertTrue(result);
        verify(scheduleRepository, times(1)).deleteById(schedule.getId());
    }

    @Test
    void testDeleteSchedule_NotFound() {
        when(scheduleRepository.existsById(anyLong())).thenReturn(false);

        boolean result = scheduleService.deleteSchedule(1L);

        assertFalse(result);
        verify(scheduleRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetSchedulesByStatus() {
        when(scheduleRepository.findByStatus(eq(ScheduleStatus.SCHEDULED))).thenReturn(List.of(schedule));

        List<ScheduleDTO> schedules = scheduleService.getSchedulesByStatus(ScheduleStatus.SCHEDULED);

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
        assertEquals(ScheduleStatus.SCHEDULED.toString(), schedules.get(0).getStatus());
    }

    @Test
    void testGetSchedulesByJobId() {
        when(scheduleRepository.findByJob_Id(eq(job.getId()))).thenReturn(List.of(schedule));

        List<ScheduleDTO> schedules = scheduleService.getSchedulesByJobId(job.getId());

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
        assertEquals(job.getId(), schedules.get(0).getJobId());
    }

    @Test
    void testGetSchedulesByMachineType() {
        when(scheduleRepository.findByMachineType(eq(schedule.getMachineType()))).thenReturn(List.of(schedule));

        List<ScheduleDTO> schedules = scheduleService.getSchedulesByMachineType(schedule.getMachineType());

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
        assertEquals(schedule.getMachineType(), schedules.get(0).getMachineType());
    }

    @Test
    void testGetSchedulesInTimeRange() {
        LocalDateTime startTime = schedule.getStartTime().minusHours(1);
        LocalDateTime endTime = schedule.getStartTime().plusMinutes(schedule.getDuration()).plusHours(1);

        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<ScheduleDTO> schedules = scheduleService.getSchedulesInTimeRange(startTime, endTime);

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
    }

    @Test
    void testIsTimeSlotAvailable_Available() {
        when(scheduleRepository.findByMachineType(eq(scheduleDTO.getMachineType()))).thenReturn(new ArrayList<>());

        boolean isAvailable = scheduleService.isTimeSlotAvailable(
                scheduleDTO.getMachineType(),
                scheduleDTO.getStartTime(),
                scheduleDTO.getStartTime().plusMinutes(scheduleDTO.getDuration())
        );

        assertTrue(isAvailable);
    }

    @Test
    void testIsTimeSlotAvailable_NotAvailable() {
        when(scheduleRepository.findByMachineType(eq(scheduleDTO.getMachineType()))).thenReturn(List.of(schedule));

        boolean isAvailable = scheduleService.isTimeSlotAvailable(
                scheduleDTO.getMachineType(),
                scheduleDTO.getStartTime(),
                scheduleDTO.getStartTime().plusMinutes(scheduleDTO.getDuration())
        );

        assertFalse(isAvailable);
    }

    @Test
    void testUpdateScheduleStatus_Success() {
        when(scheduleRepository.findById(eq(schedule.getId()))).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        ScheduleDTO updatedSchedule = scheduleService.updateScheduleStatus(schedule.getId(), ScheduleStatus.COMPLETED);

        assertNotNull(updatedSchedule);
        assertEquals(ScheduleStatus.COMPLETED.toString(), updatedSchedule.getStatus());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void testUpdateScheduleStatus_NotFound() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> scheduleService.updateScheduleStatus(1L, ScheduleStatus.COMPLETED));

        assertEquals("Schedule not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetUpcomingSchedules() {
        LocalDateTime from = LocalDateTime.now();

        when(scheduleRepository.findByStartTimeAfter(eq(from))).thenReturn(List.of(schedule));

        List<ScheduleDTO> schedules = scheduleService.getUpcomingSchedules(from);

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
    }

    @Test
    void testGetPastSchedules() {
        LocalDateTime until = LocalDateTime.now().plusDays(1);

        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<ScheduleDTO> schedules = scheduleService.getPastSchedules(until);

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
    }
}
