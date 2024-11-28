package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    List<Job> getAllJobs();
    Optional<Job> getJobById(Long id);
    Job createJob(String email, JobDTO jobDTO);
    Job updateJob(Long id, JobDTO jobDTO);
    Boolean deleteJob(Long id);

}
