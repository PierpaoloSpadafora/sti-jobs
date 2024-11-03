// IJobService.java

package unical.demacs.rdm.persistence.service.interfaces;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.persistence.dto.JobDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IJobService {
    List<JobDTO> getAllJobs();
    Optional<JobDTO> getJobById(Long id);
    JobDTO createJob(JobDTO jobDTO);
    JobDTO updateJob(Long id, JobDTO jobDTO);
    void deleteJob(Long id);

    List<JobDTO> parseJobsFromJson(MultipartFile file) throws IOException;
    ByteArrayResource exportJobs();

    Optional<JobDTO> findById(Long id);
    JobDTO saveJob(JobDTO jobDTO);
}
