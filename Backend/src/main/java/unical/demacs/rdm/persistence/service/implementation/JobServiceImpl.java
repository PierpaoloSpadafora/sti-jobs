package unical.demacs.rdm.persistence.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobServiceImpl implements IJobService {

    private final JobRepository jobRepository;

    @Override
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<JobDTO> getJobById(Long id) {
        return jobRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public JobDTO createJob(JobDTO jobDTO) {
        Job job = convertToEntity(jobDTO);
        Job savedJob = jobRepository.save(job);
        return convertToDTO(savedJob);
    }

    @Override
    public JobDTO updateJob(Long id, JobDTO jobDTO) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new UserException("Job not found"));
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        Job updatedJob = jobRepository.save(job);
        return convertToDTO(updatedJob);
    }

    @Override
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    @Override
    public Optional<JobDTO> findById(Long id) {
        return getJobById(id);
    }

    @Override
    public JobDTO saveJob(JobDTO jobDTO) {
        return createJob(jobDTO);
    }

    private JobDTO convertToDTO(Job job) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setDescription(job.getDescription());
        return jobDTO;
    }

    private Job convertToEntity(JobDTO jobDTO) {
        Job job = new Job();
        job.setId(jobDTO.getId());
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        return job;
    }
}
