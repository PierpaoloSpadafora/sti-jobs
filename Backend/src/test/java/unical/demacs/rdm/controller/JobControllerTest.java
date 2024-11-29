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
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.service.implementation.JobServiceImpl;
import org.modelmapper.ModelMapper;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
    private ObjectMapper objectMapper;
    private JobDTO jobDTO;
    private Job job;

    private static final Long TEST_ID = 1L;
    private static final String TEST_TITLE = "Test Job";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
        objectMapper = new ObjectMapper();

        jobDTO = new JobDTO();
        jobDTO.setId(TEST_ID);
        jobDTO.setTitle(TEST_TITLE);
        jobDTO.setPriority(JobPriority.MEDIUM);
        jobDTO.setStatus(JobStatus.PENDING);
        jobDTO.setIdMachineType(1L);

        job = Job.buildJob()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .priority(JobPriority.MEDIUM)
                .status(JobStatus.PENDING)
                .build();
    }

    @Test
    void testCreateJob() throws Exception {
        when(jobService.createJob(eq(TEST_EMAIL), any(JobDTO.class))).thenReturn(job);
        when(modelMapper.map(any(), eq(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(post("/api/v1/job/create-job")
                        .param("assigneeEmail", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.title").value(TEST_TITLE));

        verify(jobService, times(1)).createJob(eq(TEST_EMAIL), any(JobDTO.class));
    }

    @Test
    void testUpdateJob() throws Exception {
        when(jobService.updateJob(eq(TEST_ID), any(JobDTO.class))).thenReturn(job);
        when(modelMapper.map(any(), eq(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(put("/api/v1/job/" + TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.title").value(TEST_TITLE));

        verify(jobService, times(1)).updateJob(eq(TEST_ID), any(JobDTO.class));
    }

    @Test
    void testGetAllJobs() throws Exception {
        List<Job> jobs = Arrays.asList(job);
        when(jobService.getAllJobs()).thenReturn(jobs);
        when(modelMapper.map(any(), eq(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(get("/api/v1/job/get-all-jobs/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_ID))
                .andExpect(jsonPath("$[0].title").value(TEST_TITLE));

        verify(jobService, times(1)).getAllJobs();
    }

    @Test
    void testGetJobById() throws Exception {
        when(jobService.getJobById(TEST_ID)).thenReturn(Optional.of(job));
        when(modelMapper.map(any(), eq(JobDTO.class))).thenReturn(jobDTO);

        mockMvc.perform(get("/api/v1/job/jobs-by-id/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.title").value(TEST_TITLE));

        verify(jobService, times(1)).getJobById(TEST_ID);
    }

    @Test
    void testGetJobById_JobNotFound() throws Exception {
        when(jobService.getJobById(TEST_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/job/jobs-by-id/" + TEST_ID))
                .andExpect(status().isOk());

        verify(jobService, times(1)).getJobById(TEST_ID);
    }

    @Test
    void testDeleteJob() throws Exception {
        when(jobService.deleteJob(TEST_ID)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/job/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(jobService, times(1)).deleteJob(TEST_ID);
    }
}