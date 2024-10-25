package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unical.demacs.rdm.persistence.entities.Schedule;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByMachineId(Long machineId);
    List<Schedule> findByJobId(Long jobId);
    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}