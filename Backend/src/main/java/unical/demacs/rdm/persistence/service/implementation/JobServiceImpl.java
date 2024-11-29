package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.JobException;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineTypeRepository;
import unical.demacs.rdm.persistence.repository.UserRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobServiceImpl implements IJobService {

    public static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    private final RateLimiter rateLimiter;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final MachineTypeRepository machineTypeRepository;


    @Override
    public List<Job> getAllJobs() {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to get all jobs");
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getAllJobs");
                throw new TooManyRequestsException();
            }
            List<Job> jobs = jobRepository.findAll();
            logger.info("Jobs found: {}", jobs.size());
            return jobs;
        } catch (TooManyRequestsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting all jobs", e);
            throw new JobException("Error getting all jobs");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<Job> getJobById(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to get job by id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getJobById");
                throw new TooManyRequestsException();
            }
            Optional<Job> job = jobRepository.findById(id);
            if (job.isEmpty()) {
                logger.info("Job with id {} not found", id);
            } else {
                logger.info("Job with id {} found", id);
            }
            return job;
        } catch (TooManyRequestsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting job by id: {}", id, e);
            throw new JobException("Error getting job by id");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Boolean deleteJob(Long id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to delete job with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteJob");
                throw new TooManyRequestsException();
            }
            jobRepository.deleteById(id);
            logger.info("Job with id {} deleted successfully", id);
            return true;
        } catch (TooManyRequestsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting job with id: {}", id, e);
            throw new JobException("Error deleting job");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }
    @Override
    public Job createJob(String email, JobDTO jobDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to create job with email: {}", email);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for createJob");
                throw new TooManyRequestsException();
            }
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
            Job job = Job.buildJob()
                    .assignee(user)
                    .title(jobDTO.getTitle())
                    .priority(jobDTO.getPriority())
                    .status(jobDTO.getStatus())
                    .duration(jobDTO.getDuration())
                    .description(jobDTO.getDescription())
                    .requiredMachineType(machineTypeRepository.findById(jobDTO.getIdMachineType()).orElseThrow(() -> new JobException("Machine type not found")))
                    .build();
            jobRepository.save(job);
            logger.info("Job with email {} created successfully", email);
            return job;
        }
        catch (DataIntegrityViolationException e) {
            logger.error("Error creating job with email: {}", email, e);
            throw new JobException("Error creating job");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Job updateJob(Long id, JobDTO jobDTO) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to update job with id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for updateJob");
                throw new TooManyRequestsException();
            }
            Job job = jobRepository.findById(id).orElseThrow(() -> new JobException("Job not found"));
            job.setTitle(jobDTO.getTitle());
            job.setDescription(jobDTO.getDescription());
            job.setDuration(jobDTO.getDuration());
            job.setPriority(jobDTO.getPriority());
            job.setRequiredMachineType(machineTypeRepository.findById(jobDTO.getIdMachineType()).orElseThrow(() -> new JobException("Machine type not found")));
            jobRepository.save(job);
            logger.info("Job with id {} updated successfully", id);
            return job;
        }
        catch (DataIntegrityViolationException e) {
            logger.error("Error updating job with id: {}", id, e);
            throw new JobException("Error updating job");
        }
        finally {
            logger.info("++++++END REQUEST++++++");
        }
    }


}
