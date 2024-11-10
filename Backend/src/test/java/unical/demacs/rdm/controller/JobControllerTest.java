package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;
import unical.demacs.rdm.persistence.service.implementation.JobServiceImpl;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private JobServiceImpl jobService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private JobController jobController;

    private MockMvc mockMvc;
    private JobDTO jobDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
        objectMapper = new ObjectMapper();

        UserDTO userDTO = new UserDTO();
        userDTO.setId("1");
        userDTO.setEmail("test@example.com");

        MachineTypeDTO machineTypeDTO = new MachineTypeDTO();
        machineTypeDTO.setId(1L);
        machineTypeDTO.setName("TestMachine");
        machineTypeDTO.setDescription("Test Machine Description");

        jobDTO = new JobDTO();
        jobDTO.setId(1L);
        jobDTO.setTitle("Test Job");
        jobDTO.setDescription("Test Description");
        jobDTO.setDuration(120);
        jobDTO.setPriority(JobPriority.MEDIUM);
        jobDTO.setStatus(JobStatus.PENDING);
        jobDTO.setAssignee(userDTO);
        jobDTO.setRequiredMachineType(machineTypeDTO);
    }

    @Test
    void testCreateJob() throws Exception {
        when(jobService.createJob(any(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(post("/api/v1/job/create-job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobDTO.getId()));
    }

    @Test
    void testUpdateJob() throws Exception {
        when(jobService.updateJob(eq(1L), any(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(put("/api/v1/job/update-job1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobDTO.getId()));
    }

    @Test
    void testGetAllJobs() throws Exception {
        List<JobDTO> jobs = new ArrayList<>();
        jobs.add(jobDTO);
        when(jobService.getAllJobs()).thenReturn(jobs);
        when(modelMapper.map(any(), eq(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(get("/api/v1/job/get-all-jobs/"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetJobById() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(Optional.of(jobDTO));
        when(modelMapper.map(any(), eq(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(get("/api/v1/job/jobs-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobDTO.getId()));
    }

    @Test
    void testGetJobsByAssigneeEmail() throws Exception {
        List<JobDTO> jobs = new ArrayList<>();
        jobs.add(jobDTO);
        when(jobService.getJobByAssigneeEmail("test@example.com")).thenReturn(Optional.of(jobs));

        mockMvc.perform(get("/api/v1/job/jobs-by-assignee-email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(jobDTO.getId()));
    }

    @Test
    void testDeleteJob() throws Exception {
        mockMvc.perform(delete("/api/v1/job/delete-job1"))
                .andExpect(status().isOk());
    }
}