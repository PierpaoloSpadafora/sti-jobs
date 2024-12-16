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
    List<Schedule> findByStatusNotInAndStartTimeBefore(List<ScheduleStatus> statuses, LocalDateTime dateTime);
    List<Schedule> findByDueDateBefore(LocalDateTime date);
    List<Schedule> findByDueDateAfter(LocalDateTime date);
}