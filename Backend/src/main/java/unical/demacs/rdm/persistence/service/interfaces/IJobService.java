package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.entities.Job;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    Job createJob(Job job);
    Optional<Job> getJobById(Long id);
    List<Job> getAllJobs();

    List<Job> getJobsByAssignee(String assigneeId);

    List<Job> getJobsByMachine(Long machineId);
    Job updateJob(Job job);
    void deleteJob(Long id);
}
