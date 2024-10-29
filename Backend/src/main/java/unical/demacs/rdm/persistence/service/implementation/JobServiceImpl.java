package unical.demacs.rdm.persistence.service.implementation;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobServiceImpl implements IJobService {


    public static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    private JobRepository jobRepository;

    @Override
    public Optional<Job> getAllJobs() {
        return Optional.of((Job) jobRepository.findAll());
    }

    @Override
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    @Override
    public Job createJob(Job job) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to create job with title: {}", job.getTitle());
        try {
            Job new_job = jobRepository.findByTitle(job.getTitle()).orElse(null);
            if (new_job != null) {
                logger.info("Job with title {} already exists", job.getTitle());
                return job;
            } else {
                new_job = Job.buildJob()
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .status(job.getStatus())
                        .priority(job.getPriority())
                        .duration(job.getDuration())
                        .assignee(job.getAssignee())
                        .requiredMachineType(job.getRequiredMachineType())
                        .build();
                jobRepository.save(job);
                logger.info("Job with title {} created successfully", job.getTitle());
                return new_job;
            }
        } catch (Exception e) {
            logger.error("Error creating job with title: {}", job.getTitle(), e);
            throw new UserException("Error creating job");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<Job> updateJob(Long id, Job job) {
        return jobRepository.findById(id).map(existingJob -> {
            existingJob.setTitle(job.getTitle());
            existingJob.setDescription(job.getDescription());
            existingJob.setStatus(job.getStatus());
            existingJob.setPriority(job.getPriority());
            existingJob.setDuration(job.getDuration());
            // Assignee and MachineType mapping can be done here as needed
            return jobRepository.save(existingJob);
        });
    }

    @Override
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

}