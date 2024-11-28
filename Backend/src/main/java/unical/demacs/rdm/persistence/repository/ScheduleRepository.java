package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStatus(ScheduleStatus status);
    List<Schedule> findByJob_Id(Long jobId);
    List<Schedule> findByMachineType(String machineType);
    List<Schedule> findByStartTimeAfter(LocalDateTime startTime);
    List<Schedule> findByStartTimeBefore(LocalDateTime endTime);
    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Schedule> findByMachineTypeAndStatus(String machineType, ScheduleStatus scheduleStatus);
    @Query(value = "SELECT MAX(start_time + (duration * INTERVAL '1 second')) " +
            "FROM schedules WHERE machine_id = :machineType",
            nativeQuery = true)
    Optional<LocalDateTime> findLatestEndTimeForMachine(@Param("machineType") String machineType);
}
