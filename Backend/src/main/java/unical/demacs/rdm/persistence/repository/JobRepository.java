package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unical.demacs.rdm.persistence.dto.JobDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.enums.JobStatus;
import unical.demacs.rdm.persistence.enums.JobPriority;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByTitle(String title);
    List<Job> findByStatus(JobStatus status);
    List<Job> findByAssignee_Id(String assigneeId);
    List<Job> findByPriority(JobPriority priority);
    List<Job> findByDurationGreaterThan(java.time.Duration duration);
    List<Job> findByRequiredMachineType_Id(Long machineTypeId);
    List<Job> findByPriorityAndStatus(JobPriority priority, JobStatus status);
    List<Job> findByDurationBetween(java.time.Duration minDuration, java.time.Duration maxDuration);
    List<Job> findByAssignee_IdAndPriority(String assigneeId, JobPriority priority);
    List<Job> findByStatusAndRequiredMachineType_IdAndAssignee_Id(JobStatus status, Long machineTypeId, String assigneeId);
}