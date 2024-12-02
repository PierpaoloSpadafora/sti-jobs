package unical.demacs.rdm.service;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.repository.UserRepository;
import unical.demacs.rdm.persistence.service.implementation.JobServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MachineTypeRepository machineTypeRepository;

    @Mock
    private RateLimiter rateLimiter;

    private JobServiceImpl jobService;
    private Job testJob;
    private User testUser;
    private MachineType testMachineType;
    private JobDTO testJobDTO;

    private static final Long TEST_ID = 1L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TITLE = "Test Job";
    private static final Long TEST_MACHINE_TYPE_ID = 1L;

    @BeforeEach
    void setUp() {
        jobService = new JobServiceImpl(rateLimiter, jobRepository, userRepository, machineTypeRepository);

        testUser = User.buildUser()
                .email(TEST_EMAIL)
                .build();

        testMachineType = new MachineType();
        testMachineType.setId(TEST_MACHINE_TYPE_ID);

        testJob = Job.buildJob()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .assignee(testUser)
                .requiredMachineType(testMachineType)
                .build();

        testJobDTO = new JobDTO();
        testJobDTO.setTitle(TEST_TITLE);
        testJobDTO.setIdMachineType(TEST_MACHINE_TYPE_ID);

        when(rateLimiter.tryAcquire()).thenReturn(true);
    }

    @Test
    void testGetAllJobs_Success() {
        when(jobRepository.findAll()).thenReturn(Arrays.asList(testJob));

        List<Job> jobs = jobService.getAllJobs();

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        assertEquals(TEST_TITLE, jobs.get(0).getTitle());
        verify(jobRepository, times(1)).findAll();
    }

    @Test
    void testGetAllJobs_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> jobService.getAllJobs());
        verify(jobRepository, never()).findAll();
    }

    @Test
    void testGetJobById_Found() {
        when(jobRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testJob));

        Optional<Job> foundJob = jobService.getJobById(TEST_ID);

        assertTrue(foundJob.isPresent());
        assertEquals(TEST_TITLE, foundJob.get().getTitle());
    }

    @Test
    void testGetJobById_NotFound() {
        when(jobRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        Optional<Job> foundJob = jobService.getJobById(TEST_ID);

        assertFalse(foundJob.isPresent());
    }

    @Test
    void testGetJobById_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> jobService.getJobById(TEST_ID));
        verify(jobRepository, never()).findById(any());
    }

    @Test
    void testCreateJob_Success() {
        when(userRepository.findByEmail(eq(TEST_EMAIL))).thenReturn(Optional.of(testUser));
        when(machineTypeRepository.findById(eq(TEST_MACHINE_TYPE_ID))).thenReturn(Optional.of(testMachineType));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job createdJob = jobService.createJob(TEST_EMAIL, testJobDTO);

        assertNotNull(createdJob);
        assertEquals(TEST_TITLE, createdJob.getTitle());
    }

    @Test
    void testCreateJob_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> jobService.createJob(TEST_EMAIL, testJobDTO));
        verify(jobRepository, never()).save(any());
    }

    @Test
    void testUpdateJob_Success() {
        when(jobRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(testJob));
        when(machineTypeRepository.findById(eq(TEST_MACHINE_TYPE_ID))).thenReturn(Optional.of(testMachineType));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job updatedJob = jobService.updateJob(TEST_ID, testJobDTO);

        assertNotNull(updatedJob);
        assertEquals(TEST_TITLE, updatedJob.getTitle());
    }

    @Test
    void testUpdateJob_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> jobService.updateJob(TEST_ID, testJobDTO));
        verify(jobRepository, never()).save(any());
    }

    @Test
    void testDeleteJob_Success() {
        // Nel service viene usato deleteById, quindi dobbiamo mockare quello
        doNothing().when(jobRepository).deleteById(TEST_ID);

        assertTrue(jobService.deleteJob(TEST_ID));
        verify(jobRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    void testDeleteJob_RateLimitExceeded() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(TooManyRequestsException.class, () -> jobService.deleteJob(TEST_ID));
        verify(jobRepository, never()).deleteById(any());
    }
}