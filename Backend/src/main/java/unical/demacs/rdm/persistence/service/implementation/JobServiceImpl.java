package unical.demacs.rdm.persistence.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.dto.MachineTypeDTO;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJobService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobServiceImpl implements IJobService {

    private final JobRepository jobRepository;
    private final UserServiceImpl userServiceImpl;

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
    public Optional<List<JobDTO>> getJobByAssigneeEmail(String email) {
        List<JobDTO> jobDTOList = jobRepository.findByAssigneeEmail(email).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return jobDTOList.isEmpty() ? Optional.empty() : Optional.of(jobDTOList);
    }

    @Override
    public JobDTO createJob(JobDTO jobDTO) {
        Job job = convertToEntity(jobDTO);
        Job savedJob = jobRepository.save(job);
        return convertToDTO(savedJob);
    }

    @Override
    public JobDTO createJob(String email, JobDTO jobDTO) {
        if(email == null) {
            throw new UserException("Email is required");
        }
        Optional<User> userDTO = userServiceImpl.getUserByEmail(email);
        if(userDTO.isEmpty()) {
            throw new UserException("User not found");
        }
        jobDTO.setAssignee(new UserDTO(userDTO.get().getId(), userDTO.get().getEmail()));
        return createJob(jobDTO);
    }

    @Override
    public JobDTO updateJob(Long id, JobDTO jobDTO) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new UserException("Job not found"));
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setDuration(jobDTO.getDuration());
        job.setPriority(jobDTO.getPriority());
        job.setStatus(jobDTO.getStatus());
        MachineType mt = new MachineType();
        mt.setId(job.getRequiredMachineType().getId());
        job.setRequiredMachineType(mt);
        Job updatedJob = jobRepository.save(job);
        return convertToDTO(updatedJob);
    }

    @Override
    public void deleteJob(Long id) {
        try {
            jobRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Impossibile eliminare il job con id " + id + " perché è referenziato da altri enti", e);
        }
    }

    @Override
    public Optional<JobDTO> findById(Long id) {
        return getJobById(id);
    }

    @Override
    public void saveJob(JobDTO jobDTO) {
        createJob(jobDTO);
    }

    private JobDTO convertToDTO(Job job) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setDuration(job.getDuration());
        jobDTO.setPriority(job.getPriority());
        jobDTO.setStatus(job.getStatus());
        UserDTO u = new UserDTO();
        u.setId(job.getAssignee().getId());
        u.setEmail(job.getAssignee().getEmail());
        jobDTO.setAssignee(u);
        MachineTypeDTO mt = new MachineTypeDTO();
        mt.setId(job.getRequiredMachineType().getId());
        mt.setName(job.getRequiredMachineType().getName());
        mt.setDescription(job.getRequiredMachineType().getDescription());
        jobDTO.setRequiredMachineType(mt);
        return jobDTO;
    }

    private Job convertToEntity(JobDTO jobDTO) {
        Job job = new Job();
        job.setId(jobDTO.getId());
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setDuration(jobDTO.getDuration());
        job.setPriority(jobDTO.getPriority());
        job.setStatus(jobDTO.getStatus());
        User u = new User();
        u.setId(jobDTO.getAssignee().getId());
        u.setEmail(jobDTO.getAssignee().getEmail());
        job.setAssignee(u);
        MachineType mt = new MachineType();
        mt.setId(jobDTO.getRequiredMachineType().getId());
        mt.setName(jobDTO.getRequiredMachineType().getName());
        mt.setDescription(jobDTO.getRequiredMachineType().getDescription());
        job.setRequiredMachineType(mt);
        return job;
    }

    @Override
    @Transactional
    public void deleteJobByMachineType(Long machineTypeId) {
        jobRepository.deleteByRequiredMachineType_Id(machineTypeId);
    }
}
