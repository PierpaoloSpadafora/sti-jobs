package unical.demacs.rdm.persistence.service.implementation;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JobServiceImpl implements IJobService {
    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    private  JobRepository jobRepository;

    @Override
    public Job createJob(Job job) {
        logger.info("Creating new job: {}", job.getTitle());
        return jobRepository.save(job);
    }

    @Override
    public Optional<Job> getJobById(Long id) {
        logger.info("Fetching job with id: {}", id);
        return jobRepository.findById(id);
    }

    @Override
    public List<Job> getAllJobs() {
        logger.info("Fetching all jobs");
        return jobRepository.findAll();
    }

    @Override
    public List<Job> getJobsByAssignee(String assigneeId) {
        logger.info("Fetching jobs for assignee: {}", assigneeId);
        return jobRepository.findByAssigneeId(assigneeId);
    }

    @Override
    public List<Job> getJobsByMachine(Long machineId) {
        logger.info("Fetching jobs for machine: {}", machineId);
        return jobRepository.findByMachineId(machineId);
    }

    @Override
    public Job updateJob(Job job) {
        logger.info("Updating job with id: {}", job.getId());
        return jobRepository.save(job);
    }

    @Override
    public void deleteJob(Long id) {
        logger.info("Deleting job with id: {}", id);
        jobRepository.deleteById(id);
    }
}