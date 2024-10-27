package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.entities.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IScheduleService {
    Schedule createSchedule(Schedule schedule);
    Optional<Schedule> getScheduleById(Long id);
    List<Schedule> getSchedulesByMachine(Long machineId);
    List<Schedule> getSchedulesByJob(Long jobId);
    List<Schedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end);
    Schedule updateSchedule(Schedule schedule);
    void deleteSchedule(Long id);
}
