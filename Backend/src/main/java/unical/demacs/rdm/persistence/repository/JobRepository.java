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
    // Qui è possibile definire metodi di query personalizzati se necessario

    // Trova lavori in base allo status
    Optional<Job> findByTitle(String title);

    // Trova lavori in base allo status
    List<Job> findByStatus(JobStatus status);

    // Trova lavori assegnati a un particolare utente (assignee)
    List<Job> findByAssignee_Id(String assigneeId);

    // Trova lavori con una certa priorità
    List<Job> findByPriority(JobPriority priority);

    // Trova lavori con una durata maggiore di una certa durata
    List<Job> findByDurationGreaterThan(java.time.Duration duration);

    // Trova lavori creati per un determinato tipo di macchina
    List<Job> findByRequiredMachineType_Id(Long machineTypeId);

    // Trova lavori con una certa priorità e status
    List<Job> findByPriorityAndStatus(JobPriority priority, JobStatus status);

    // Trova lavori con durata compresa tra due valori
    List<Job> findByDurationBetween(java.time.Duration minDuration, java.time.Duration maxDuration);

    // Trova lavori assegnati a un utente specifico e con una certa priorità
    List<Job> findByAssignee_IdAndPriority(String assigneeId, JobPriority priority);

    // Trova lavori creati dopo una certa data e con un certo status
    List<Job> findByStatusAndRequiredMachineType_IdAndAssignee_Id(JobStatus status, Long machineTypeId, String assigneeId);
}