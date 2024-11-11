package unical.demacs.rdm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.implementation.JobServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private JobDTO jobDTO;
    private Job job;
    private User user;
    private MachineType machineType;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setEmail("test@example.com");

        machineType = new MachineType();
        machineType.setId(1L);
        machineType.setName("TestMachine");
        machineType.setDescription("Test Machine Description");

        job = new Job();
        job.setId(1L);
        job.setTitle("Test Job");
        job.setDescription("Test Description");
        job.setDuration(120);
        job.setPriority(JobPriority.MEDIUM);
        job.setStatus(JobStatus.PENDING);
        job.setAssignee(user);
        job.setRequiredMachineType(machineType);

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
    void testGetAllJobs() {
        when(jobRepository.findAll()).thenReturn(List.of(job));
        List<JobDTO> jobs = jobService.getAllJobs();
        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        assertEquals(job.getId(), jobs.get(0).getId());
    }

    @Test
    void testGetJobById() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        Optional<JobDTO> foundJob = jobService.getJobById(1L);
        assertTrue(foundJob.isPresent());
        assertEquals(job.getId(), foundJob.get().getId());
    }

    @Test
    void testGetJobByAssigneeEmail() {
        when(jobRepository.findByAssigneeEmail("test@example.com")).thenReturn(List.of(job));
        Optional<List<JobDTO>> jobs = jobService.getJobByAssigneeEmail("test@example.com");
        assertTrue(jobs.isPresent());
        assertEquals(1, jobs.get().size());
        assertEquals(job.getId(), jobs.get().get(0).getId());
    }

    @Test
    void testCreateJob() {
        when(jobRepository.save(any(Job.class))).thenReturn(job);
        JobDTO createdJob = jobService.createJob(jobDTO);
        assertNotNull(createdJob);
        assertEquals(jobDTO.getId(), createdJob.getId());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void testUpdateJob_Success() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenReturn(job);
        JobDTO updatedJob = jobService.updateJob(1L, jobDTO);
        assertNotNull(updatedJob);
        assertEquals(jobDTO.getId(), updatedJob.getId());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void testUpdateJob_NotFound() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> jobService.updateJob(1L, jobDTO));
    }

    @Test
    void testDeleteJob() {
        jobService.deleteJob(1L);
        verify(jobRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindById() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        Optional<JobDTO> foundJob = jobService.findById(1L);
        assertTrue(foundJob.isPresent());
        assertEquals(job.getId(), foundJob.get().getId());
    }

    @Test
    void testSaveJob() {
        when(jobRepository.save(any(Job.class))).thenReturn(job);
        jobService.saveJob(jobDTO);
        verify(jobRepository, times(1)).save(any(Job.class));
    }
}