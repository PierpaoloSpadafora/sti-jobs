package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.service.interfaces.IScheduleService;
import unical.demacs.rdm.utils.Scheduler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IScheduleService scheduleService;

    @MockBean
    private Scheduler scheduler;

    @MockBean(name = "modelMapper")
    private ModelMapper modelMapper;

    @MockBean(name = "modelMapperExtended")
    private ModelMapperExtended modelMapperExtended;

    private Schedule schedule;
    private ScheduleDTO scheduleDTO;
    private static final Long TEST_ID = 1L;
    private static final LocalDateTime NOW = LocalDateTime.now();

    private ObjectMapper objectMapper;

    private static final Long TEST_JOB_ID = 1L;
    private static final Long TEST_MACHINE_TYPE_ID = 1L;
    private static final LocalDateTime TEST_START_TIME = LocalDateTime.now();
    private static final LocalDateTime TEST_DUE_DATE = LocalDateTime.now().plusDays(1);
    private static final int TEST_DURATION = 60;

    @BeforeEach
    public void setUp() {
        schedule = Schedule.scheduleBuilder()
                .id(TEST_ID)
                .startTime(NOW)
                .dueDate(NOW.plusDays(1))
                .duration(60L)
                .status(ScheduleStatus.PENDING)
                .build();

        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(TEST_ID);
        scheduleDTO.setJobId(TEST_JOB_ID);
        scheduleDTO.setMachineTypeId(TEST_MACHINE_TYPE_ID);
        scheduleDTO.setStartTime(TEST_START_TIME);
        scheduleDTO.setDueDate(TEST_DUE_DATE);
        scheduleDTO.setDuration(60L);
        scheduleDTO.setStatus(ScheduleStatus.PENDING);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
    }
    @Test
    public void testCreateSchedule() throws Exception {
        when(scheduleService.createSchedule(any(ScheduleDTO.class))).thenReturn(schedule);
        when(modelMapper.map(any(Schedule.class), eq(ScheduleDTO.class))).thenReturn(scheduleDTO);

        String scheduleDTOJson = objectMapper.writeValueAsString(scheduleDTO);

        mockMvc.perform(post("/api/v1/schedules/create-schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleDTOJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.jobId").value(TEST_JOB_ID))
                .andExpect(jsonPath("$.machineTypeId").value(TEST_MACHINE_TYPE_ID))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.dueDate").exists())
                .andExpect(jsonPath("$.duration").value(TEST_DURATION))
                .andExpect(jsonPath("$.status").value(ScheduleStatus.PENDING.toString()));

        verify(scheduleService).createSchedule(any(ScheduleDTO.class));
    }

    @Test
    public void testUpdateScheduleStatus() throws Exception {
        when(scheduleService.updateScheduleStatus(eq(TEST_ID), eq(ScheduleStatus.COMPLETED))).thenReturn(schedule);
        when(modelMapper.map(any(Schedule.class), eq(ScheduleDTO.class))).thenReturn(scheduleDTO);

        mockMvc.perform(patch("/api/v1/schedules/{id}/status", TEST_ID)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID));
    }

    @Test
    public void testGetScheduleById() throws Exception {
        when(scheduleService.getScheduleById(TEST_ID)).thenReturn(Optional.of(schedule));
        when(modelMapper.map(any(Schedule.class), eq(ScheduleDTO.class))).thenReturn(scheduleDTO);

        mockMvc.perform(get("/api/v1/schedules/{id}", TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID));
    }

    @Test
    void testGetAllSchedules() throws Exception {
        List<Schedule> schedules = Collections.singletonList(schedule);
        List<ScheduleDTO> scheduleDTOs = Collections.singletonList(scheduleDTO);

        when(scheduleService.getAllSchedules()).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/get-all-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getAllSchedules();
    }

    @Test
    void testGetSchedulesByStatus() throws Exception {
        List<Schedule> schedules = Collections.singletonList(schedule);
        List<ScheduleDTO> scheduleDTOs = Collections.singletonList(scheduleDTO);

        when(scheduleService.getSchedulesByStatus(eq(ScheduleStatus.PENDING))).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getSchedulesByStatus(ScheduleStatus.PENDING);
    }

    @Test
    void testGetSchedulesByJobId() throws Exception {
        List<Schedule> schedules = Collections.singletonList(schedule);
        List<ScheduleDTO> scheduleDTOs = Collections.singletonList(scheduleDTO);

        when(scheduleService.getSchedulesByJobId(TEST_JOB_ID)).thenReturn(schedules);
        when(modelMapperExtended.mapList(eq(schedules), eq(ScheduleDTO.class))).thenReturn(scheduleDTOs);

        mockMvc.perform(get("/api/v1/schedules/job/" + TEST_JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID));

        verify(scheduleService).getSchedulesByJobId(TEST_JOB_ID);
    }

    @Test
    void testGetSchedulesInTimeRange() throws Exception {
        List<Schedule> schedules = Collections.singletonList(schedule);
        List<ScheduleDTO> scheduleDTOs = Collections.singletonList(scheduleDTO);
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
        List<Schedule> schedules = Collections.singletonList(schedule);
        List<ScheduleDTO> scheduleDTOs = Collections.singletonList(scheduleDTO);
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
        List<Schedule> schedules = Collections.singletonList(schedule);
        List<ScheduleDTO> scheduleDTOs = Collections.singletonList(scheduleDTO);
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
