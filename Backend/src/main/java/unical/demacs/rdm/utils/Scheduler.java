package unical.demacs.rdm.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.persistence.dto.ScheduleViewDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.repository.JobRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Scheduler {
    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;

    @Transactional
    public List<ScheduleViewDTO> scheduleByPriority(String machineType) {
        List<Schedule> pendingSchedules = scheduleRepository.findByMachineTypeAndStatus(
                machineType,
                ScheduleStatus.PENDING
        );

        if (!pendingSchedules.isEmpty()) {
            LocalDateTime startTime = scheduleRepository.findLatestEndTimeForMachine(machineType)
                    .orElse(LocalDateTime.now());

            List<Schedule> sortedSchedules = pendingSchedules.stream()
                    .sorted(Comparator.comparing(schedule -> schedule.getJob().getPriority(), Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            for (Schedule schedule : sortedSchedules) {
                schedule.setStartTime(startTime);
                schedule.setStatus(ScheduleStatus.SCHEDULED);
                scheduleRepository.save(schedule);
                startTime = startTime.plusMinutes(schedule.getDuration());
            }
        }

        return getScheduleTimeline(machineType);
    }

    private List<ScheduleViewDTO> getScheduleTimeline(String machineType) {
        List<Schedule> allSchedules = scheduleRepository.findByMachineType(machineType);

        return allSchedules.stream()
                .map(schedule -> {
                    ScheduleViewDTO viewDTO = new ScheduleViewDTO();
                    viewDTO.setId(schedule.getId());
                    viewDTO.setJobId(schedule.getJob().getId());
                    viewDTO.setJobName(schedule.getJob().getTitle());
                    viewDTO.setMachineType(schedule.getMachineType());
                    viewDTO.setStartTime(schedule.getStartTime());
                    viewDTO.setEndTime(schedule.getStartTime().plusMinutes(schedule.getDuration()));
                    viewDTO.setDuration(schedule.getDuration());
                    viewDTO.setPriority(schedule.getJob().getPriority());
                    return viewDTO;
                })
                .sorted(Comparator.comparing(ScheduleViewDTO::getStartTime))
                .collect(Collectors.toList());
    }
}