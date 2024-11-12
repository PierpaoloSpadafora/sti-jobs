package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStatus(ScheduleStatus status);
    List<Schedule> findByJob_Id(Long jobId);
    List<Schedule> findByMachineType(String machineType);
    List<Schedule> findByStartTimeAfter(LocalDateTime startTime);
    List<Schedule> findByStartTimeBefore(LocalDateTime endTime);
    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
