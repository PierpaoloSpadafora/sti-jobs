package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.entities.Job;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    List<Job> getAllJobs();
    Optional<Job> getJobById(Long id);
    Job createJob(Job job);
    Job updateJob(Long id, Job job);
    void deleteJob(Long id);
}
