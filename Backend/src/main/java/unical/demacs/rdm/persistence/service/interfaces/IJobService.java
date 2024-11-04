package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.JobDTO;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    List<JobDTO> getAllJobs();
    Optional<JobDTO> getJobById(Long id);
    JobDTO createJob(JobDTO jobDTO);
    JobDTO updateJob(Long id, JobDTO jobDTO);
    void deleteJob(Long id);

    Optional<JobDTO> findById(Long id);
    JobDTO saveJob(JobDTO jobDTO);
}