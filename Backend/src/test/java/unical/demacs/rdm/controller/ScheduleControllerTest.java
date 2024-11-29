package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.config.exception.handler.ExceptionsHandler;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.service.implementation.ScheduleServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {

    @Mock
    private ScheduleServiceImpl scheduleService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ModelMapperExtended modelMapperExtended;

    @InjectMocks
    private ScheduleController scheduleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ScheduleDTO scheduleDTO;
    private Schedule schedule;
    private Job job;
    private MachineType machineType;

    private static final Long TEST_ID = 1L;
    private static final Long TEST_JOB_ID = 1L;
    private static final Long TEST_MACHINE_TYPE_ID = 1L;
    private static final LocalDateTime TEST_START_TIME = LocalDateTime.now();
    private static final LocalDateTime TEST_DUE_DATE = LocalDateTime.now().plusDays(1);
    private static final int TEST_DURATION = 60;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController)
                .setControllerAdvice(new ExceptionsHandler(objectMapper))
                .build();

        job = new Job();
        job.setId(TEST_JOB_ID);

        machineType = MachineType.buildMachineType()
                .id(TEST_MACHINE_TYPE_ID)
                .build();

        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(TEST_ID);
        scheduleDTO.setJobId(TEST_JOB_ID);
        scheduleDTO.setMachineTypeId(TEST_MACHINE_TYPE_ID);
        scheduleDTO.setStartTime(TEST_START_TIME);
        scheduleDTO.setDueDate(TEST_DUE_DATE);
        scheduleDTO.setDuration((long) TEST_DURATION);

        schedule = Schedule.scheduleBuilder()
                .id(TEST_ID)
                .job(job)
                .machineType(machineType)
                .startTime(TEST_START_TIME)
                .dueDate(TEST_DUE_DATE)
                .duration((long) TEST_DURATION)
                .build();
    }

    @Test
    void testCreateSchedule() throws Exception {
        when(scheduleService.createSchedule(any(ScheduleDTO.class))).thenReturn(schedule);
        when(modelMapper.map(eq(schedule), eq(ScheduleDTO.class))).thenReturn(scheduleDTO);

        mockMvc.perform(post("/api/v1/schedules/create-schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.jobId").value(TEST_JOB_ID));

        verify(scheduleService).createSchedule(any(ScheduleDTO.class));
    }

    @Test
    void testUpdateScheduleStatus() throws Exception {
        String newStatus = "COMPLETED";
        when(scheduleService.updateScheduleStatus(eq(TEST_ID), eq(ScheduleStatus.COMPLETED))).thenReturn(schedule);
        when(modelMapper.map(eq(schedule), eq(ScheduleDTO.class))).thenReturn(scheduleDTO);

        mockMvc.perform(patch("/api/v1/schedules/" + TEST_ID + "/status")
                        .param("status", newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID));

        verify(scheduleService).updateScheduleStatus(eq(TEST_ID), eq(ScheduleStatus.COMPLETED));
    }

    @Test
    void testGetScheduleById() throws Exception {
        when(scheduleService.getScheduleById(TEST_ID)).thenReturn(Optional.of(schedule));
        when(modelMapper.map(eq(schedule), eq(ScheduleDTO.class))).thenReturn(scheduleDTO);

        mockMvc.perform(get("/api/v1/schedules/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID));

        verify(scheduleService).getScheduleById(TEST_ID);
    }

    @Test
    void testGetAllSchedules() throws Exception {
        List<Schedule> schedules = Arrays.asList(schedule);
        List<ScheduleDTO> scheduleDTOs = Arrays.asList(scheduleDTO);

        when(scheduleService.getAllSchedules()).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/get-all-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getAllSchedules();
    }

    @Test
    void testGetSchedulesByStatus() throws Exception {
        List<Schedule> schedules = Arrays.asList(schedule);
        List<ScheduleDTO> scheduleDTOs = Arrays.asList(scheduleDTO);

        when(scheduleService.getSchedulesByStatus(eq(ScheduleStatus.PENDING))).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getSchedulesByStatus(ScheduleStatus.PENDING);
    }

    @Test
    void testGetSchedulesByJobId() throws Exception {
        List<Schedule> schedules = Arrays.asList(schedule);
        List<ScheduleDTO> scheduleDTOs = Arrays.asList(scheduleDTO);

        when(scheduleService.getSchedulesByJobId(TEST_JOB_ID)).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/job/" + TEST_JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getSchedulesByJobId(TEST_JOB_ID);
    }

    @Test
    void testGetSchedulesInTimeRange() throws Exception {
        List<Schedule> schedules = Arrays.asList(schedule);
        List<ScheduleDTO> scheduleDTOs = Arrays.asList(scheduleDTO);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);

        when(scheduleService.getSchedulesInTimeRange(any(), any())).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/timeRange")
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getSchedulesInTimeRange(any(), any());
    }

    @Test
    void testCheckTimeSlotAvailability() throws Exception {
        String machineType = "TestMachine";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);

        when(scheduleService.isTimeSlotAvailable(eq(machineType), any(), any())).thenReturn(true);

        mockMvc.perform(get("/api/v1/schedules/availability")
                        .param("machineType", machineType)
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(scheduleService).isTimeSlotAvailable(eq(machineType), any(), any());
    }

    @Test
    void testGetUpcomingSchedules() throws Exception {
        List<Schedule> schedules = Arrays.asList(schedule);
        List<ScheduleDTO> scheduleDTOs = Arrays.asList(scheduleDTO);
        LocalDateTime from = LocalDateTime.now();

        when(scheduleService.getUpcomingSchedules(any())).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/upcoming")
                        .param("from", from.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getUpcomingSchedules(any());
    }

    @Test
    void testGetPastSchedules() throws Exception {
        List<Schedule> schedules = Arrays.asList(schedule);
        List<ScheduleDTO> scheduleDTOs = Arrays.asList(scheduleDTO);
        LocalDateTime until = LocalDateTime.now();

        when(scheduleService.getPastSchedules(any())).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/past")
                        .param("until", until.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getPastSchedules(any());
    }

    @Test
    void testDeleteSchedule() throws Exception {
        when(scheduleService.deleteSchedule(TEST_ID)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/schedules/" + TEST_ID))
                .andExpect(status().isNoContent());

        verify(scheduleService).deleteSchedule(TEST_ID);
    }

    @Test
    void testGetScheduleById_NotFound() throws Exception {
        when(scheduleService.getScheduleById(TEST_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/schedules/" + TEST_ID))
                .andExpect(status().isNotFound());

        verify(scheduleService).getScheduleById(TEST_ID);
    }
}