package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    Optional<Job> getAllJobs();
    Optional<Job> getJobById(Long id);
    Job createJob(Job job);
    Optional<Job> updateJob(Long id, Job job);
    void deleteJob(Long id);
}
