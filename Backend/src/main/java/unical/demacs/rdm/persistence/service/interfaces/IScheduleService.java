package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IScheduleService {
    Schedule createSchedule(ScheduleDTO scheduleDTO);
    Schedule updateScheduleStatus(Long id, ScheduleStatus newStatus);
    Schedule updateSchedule(Long id, ScheduleDTO scheduleDTO);
    boolean deleteSchedule(Long id);

    List<Schedule> getSchedulesByStatus(ScheduleStatus status);
    List<Schedule> getSchedulesByJobId(Long jobId);
    List<Schedule> getSchedulesByMachineType(String machineType);
    List<Schedule> getSchedulesInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    Optional<Schedule> getScheduleById(Long id);
    List<Schedule> getAllSchedules();
    boolean isTimeSlotAvailable(String machineType, LocalDateTime startTime, LocalDateTime endTime);

    List<Schedule> getUpcomingSchedules(LocalDateTime from);
    List<Schedule> getPastSchedules(LocalDateTime until);
}
