package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IScheduleService {
    ScheduleDTO createSchedule(ScheduleDTO scheduleDTO);
    Optional<ScheduleDTO> getScheduleById(Long id);
    List<ScheduleDTO> getAllSchedules();
    ScheduleDTO updateSchedule(Long id, ScheduleDTO scheduleDTO);
    boolean deleteSchedule(Long id);

    List<ScheduleDTO> getSchedulesByStatus(ScheduleStatus status);
    List<ScheduleDTO> getSchedulesByJobId(Long jobId);
    List<ScheduleDTO> getSchedulesByMachineId(Long machineId);
    List<ScheduleDTO> getSchedulesInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    boolean isTimeSlotAvailable(Long machineId, LocalDateTime startTime, LocalDateTime endTime);
    ScheduleDTO updateScheduleStatus(Long id, ScheduleStatus newStatus);
    List<ScheduleDTO> getUpcomingSchedules(LocalDateTime from);
    List<ScheduleDTO> getPastSchedules(LocalDateTime until);
}