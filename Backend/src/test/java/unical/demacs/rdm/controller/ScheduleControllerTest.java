package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.service.interfaces.IScheduleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {

    @Mock
    private IScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private MockMvc mockMvc;

    private ScheduleDTO scheduleDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();

        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(1L);
        scheduleDTO.setJobId(1L);
        scheduleDTO.setMachineType("TypeA");
        scheduleDTO.setDueDate(LocalDateTime.now().plusDays(1));
        scheduleDTO.setStartTime(LocalDateTime.now());
        scheduleDTO.setDuration(120L); // Durata in minuti
        scheduleDTO.setStatus(ScheduleStatus.SCHEDULED.toString());
    }

    @Test
    void testCreateSchedule() throws Exception {
        when(scheduleService.createSchedule(any(ScheduleDTO.class))).thenReturn(scheduleDTO);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc.perform(post("/api/v1/schedules/create-schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(scheduleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(scheduleDTO.getId()));
    }

    @Test
    void testCreateSchedule_BadRequest() throws Exception {
        when(scheduleService.createSchedule(any(ScheduleDTO.class))).thenThrow(new IllegalArgumentException());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc.perform(post("/api/v1/schedules/create-schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(scheduleDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateScheduleStatus() throws Exception {
        scheduleDTO.setStatus(ScheduleStatus.COMPLETED.toString());
        when(scheduleService.updateScheduleStatus(eq(1L), eq(ScheduleStatus.COMPLETED))).thenReturn(scheduleDTO);

        mockMvc.perform(patch("/api/v1/schedules/1/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(scheduleDTO.getStatus()));
    }

    @Test
    void testUpdateScheduleStatus_BadRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/schedules/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSchedule_Success() throws Exception {
        when(scheduleService.deleteSchedule(eq(1L))).thenReturn(true);

        mockMvc.perform(delete("/api/v1/schedules/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSchedule_NotFound() throws Exception {
        when(scheduleService.deleteSchedule(eq(1L))).thenReturn(false);

        mockMvc.perform(delete("/api/v1/schedules/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllSchedules() throws Exception {
        List<ScheduleDTO> schedules = new ArrayList<>();
        schedules.add(scheduleDTO);

        when(scheduleService.getAllSchedules()).thenReturn(schedules);

        mockMvc.perform(get("/api/v1/schedules/get-all-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(scheduleDTO.getId()));
    }

    @Test
    void testGetSchedulesByStatus() throws Exception {
        List<ScheduleDTO> schedules = new ArrayList<>();
        schedules.add(scheduleDTO);

        when(scheduleService.getSchedulesByStatus(eq(ScheduleStatus.SCHEDULED))).thenReturn(schedules);

        mockMvc.perform(get("/api/v1/schedules/status/SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(scheduleDTO.getStatus()));
    }

    @Test
    void testGetSchedulesByStatus_BadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCheckTimeSlotAvailability() throws Exception {
        when(scheduleService.isTimeSlotAvailable(eq("TypeA"), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

        mockMvc.perform(get("/api/v1/schedules/availability")
                        .param("machineType", "TypeA")
                        .param("startTime", LocalDateTime.now().toString())
                        .param("endTime", LocalDateTime.now().plusHours(2).toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
