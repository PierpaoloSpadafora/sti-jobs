package unical.demacs.rdm.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unical.demacs.rdm.persistence.dto.ScheduleViewDTO;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;
import unical.demacs.rdm.persistence.repository.JobRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
public class Scheduler {
    private final ScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;

    @Transactional
    public List<ScheduleViewDTO> scheduleByPriority(Long machineType) {
        List<Schedule> pendingSchedules = scheduleRepository.findByMachineType_IdAndStatus(
                machineType,
                ScheduleStatus.PENDING
        );

        if (pendingSchedules.isEmpty()) {
            return getScheduleTimeline(machineType);
        }

        List<Schedule> existingSchedules = scheduleRepository.findByMachineType_IdAndStatus(
                machineType,
                ScheduleStatus.SCHEDULED
        );

        List<TimeWindow> availableWindows = findAvailableTimeWindows(existingSchedules);
        LocalDateTime currentTime = LocalDateTime.now();

        if (availableWindows.isEmpty()) {
            availableWindows.add(new TimeWindow(currentTime, null));
        }

        List<Schedule> sortedPendingSchedules = pendingSchedules.stream()
                .sorted(Comparator.comparing((Schedule s) -> s.getJob().getPriority()).reversed())
                .collect(Collectors.toList());

        for (Schedule schedule : sortedPendingSchedules) {
            Long durationMinutes = schedule.getDuration();
            boolean scheduled = false;

            for (TimeWindow window : availableWindows) {
                LocalDateTime windowStart = window.getStart();
                LocalDateTime windowEnd = window.getEnd();

                if (windowStart.isBefore(currentTime)) {
                    windowStart = currentTime;
                }

                if (windowEnd == null || windowStart.plusSeconds(durationMinutes).isBefore(windowEnd)) {
                    schedule.setStartTime(windowStart);
                    schedule.setStatus(ScheduleStatus.SCHEDULED);
                    scheduleRepository.save(schedule);

                    window.setStart(windowStart.plusSeconds(durationMinutes));
                    scheduled = true;
                    break;
                }
            }

            if (!scheduled && !availableWindows.isEmpty()) {
                TimeWindow lastWindow = availableWindows.get(availableWindows.size() - 1);
                LocalDateTime startTime = lastWindow.getEnd() != null ?
                        lastWindow.getEnd() : lastWindow.getStart();

                schedule.setStartTime(startTime);
                schedule.setStatus(ScheduleStatus.SCHEDULED);
                scheduleRepository.save(schedule);
            }
        }

        return getScheduleTimeline(machineType);
    }

    @Data
    @AllArgsConstructor
    private static class TimeWindow {
        private LocalDateTime start;
        private LocalDateTime end;
    }

    private List<TimeWindow> findAvailableTimeWindows(List<Schedule> existingSchedules) {
        List<TimeWindow> windows = new ArrayList<>();
        if (existingSchedules.isEmpty()) {
            return windows;
        }

        existingSchedules.sort(Comparator.comparing(Schedule::getStartTime));

        LocalDateTime lastEndTime = null;
        for (Schedule schedule : existingSchedules) {
            LocalDateTime scheduleStart = schedule.getStartTime();
            LocalDateTime scheduleEnd = schedule.getStartTime().plusSeconds(schedule.getDuration());

            if (lastEndTime != null && scheduleStart.isAfter(lastEndTime)) {
                windows.add(new TimeWindow(lastEndTime, scheduleStart));
            }

            lastEndTime = scheduleEnd;
        }

        if (lastEndTime != null) {
            windows.add(new TimeWindow(lastEndTime, null));
        }

        return windows;
    }

    private List<ScheduleViewDTO> getScheduleTimeline(Long machineType) {
        List<Schedule> allSchedules = scheduleRepository.findByMachineType_Id(machineType);

        return allSchedules.stream()
                .map(schedule -> {
                    ScheduleViewDTO viewDTO = new ScheduleViewDTO();
                    viewDTO.setId(schedule.getId());
                    viewDTO.setJobId(schedule.getJob().getId());
                    viewDTO.setJobName(schedule.getJob().getTitle());
                    viewDTO.setMachineType(schedule.getMachineType().getName());
                    viewDTO.setStartTime(schedule.getStartTime());
                    viewDTO.setEndTime(schedule.getStartTime().plusSeconds(schedule.getDuration()));
                    viewDTO.setDuration(schedule.getDuration());
                    viewDTO.setPriority(schedule.getJob().getPriority());
                    return viewDTO;
                })
                .sorted(Comparator.comparing(ScheduleViewDTO::getPriority).reversed()
                        .thenComparing(ScheduleViewDTO::getStartTime))
                .collect(Collectors.toList());
    }
}