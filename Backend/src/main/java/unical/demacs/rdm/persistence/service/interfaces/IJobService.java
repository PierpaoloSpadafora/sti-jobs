package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.JobDTO;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    List<JobDTO> getAllJobs();
    Optional<JobDTO> getJobById(Long id);
    Optional<List<JobDTO>> getJobByAssigneeEmail(String email);
    JobDTO createJob(JobDTO jobDTO);
    JobDTO createJob(String email,JobDTO jobDTO);
    JobDTO updateJob(Long id, JobDTO jobDTO);
    void deleteJob(Long id);
    void deleteJobByMachineType(Long machineTypeId);

    Optional<JobDTO> findById(Long id);
    void saveJob(JobDTO jobDTO);
}
