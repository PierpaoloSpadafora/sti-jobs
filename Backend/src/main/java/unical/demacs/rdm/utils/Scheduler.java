package unical.demacs.rdm.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.*;
import unical.demacs.rdm.persistence.repository.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Scheduler {
    private final ScheduleRepository scheduleRepository;
    private final ModelMapperExtended modelMapperExtended;
    private final ObjectMapper objectMapper;

    public void scheduleByEveryType() {
        List<Schedule> originalSchedules = scheduleRepository.findAll();

        List<Schedule> prioritySchedules = cloneSchedules(originalSchedules);
        List<Schedule> dueDateSchedules = cloneSchedules(originalSchedules);
        List<Schedule> durationSchedules = cloneSchedules(originalSchedules);

        List<Schedule> priorityResult = scheduleByPriority(prioritySchedules);
        List<Schedule> dueDateResult = scheduleByDueDate(dueDateSchedules);
        List<Schedule> durationResult = scheduleByDuration(durationSchedules);

        saveSchedulesToFile(priorityResult, "priority");
        saveSchedulesToFile(dueDateResult, "due-date");
        saveSchedulesToFile(durationResult, "duration");
    }

    private List<Schedule> cloneSchedules(List<Schedule> schedules) {
        return schedules.stream()
                .map(s -> Schedule.scheduleBuilder()
                        .id(s.getId())
                        .job(s.getJob())
                        .machineType(s.getMachineType())
                        .dueDate(s.getDueDate())
                        .startTime(s.getStartTime())
                        .duration(s.getDuration())
                        .status(s.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Schedule> scheduleByPriority(List<Schedule> schedules) {
        Map<MachineType, List<Schedule>> schedulesByType = groupSchedulesByMachineType(schedules);
        List<Schedule> result = new ArrayList<>();

        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByType.entrySet()) {
            List<Schedule> typeSchedules = entry.getValue();

            typeSchedules.sort((s1, s2) -> {
                int priority1 = s1.getJob().getPriority().ordinal();
                int priority2 = s2.getJob().getPriority().ordinal();
                return Integer.compare(priority2, priority1);
            });

            reorganizeScheduleTimes(typeSchedules);
            result.addAll(typeSchedules);
        }

        return result;
    }

    private List<Schedule> scheduleByDueDate(List<Schedule> schedules) {
        Map<MachineType, List<Schedule>> schedulesByType = groupSchedulesByMachineType(schedules);
        List<Schedule> result = new ArrayList<>();

        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByType.entrySet()) {
            List<Schedule> typeSchedules = entry.getValue();

            typeSchedules.sort(Comparator.comparing(Schedule::getDueDate,
                    Comparator.nullsLast(Comparator.naturalOrder())));

            reorganizeScheduleTimes(typeSchedules);
            result.addAll(typeSchedules);
        }

        return result;
    }

    private List<Schedule> scheduleByDuration(List<Schedule> schedules) {
        Map<MachineType, List<Schedule>> schedulesByType = groupSchedulesByMachineType(schedules);
        List<Schedule> result = new ArrayList<>();

        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByType.entrySet()) {
            List<Schedule> typeSchedules = entry.getValue();

            typeSchedules.sort(Comparator.comparing(Schedule::getDuration));

            reorganizeScheduleTimes(typeSchedules);
            result.addAll(typeSchedules);
        }

        return result;
    }

    private void reorganizeScheduleTimes(List<Schedule> schedules) {
        if (schedules.isEmpty()) return;

        // Prendiamo il primo startTime come riferimento
        LocalDateTime currentTime = schedules.get(0).getStartTime();

        for (Schedule schedule : schedules) {
            // Impostiamo il nuovo startTime
            schedule.setStartTime(currentTime);

            // Calcoliamo il prossimo startTime disponibile
            currentTime = currentTime.plusSeconds(schedule.getDuration());
        }
    }

    private Map<MachineType, List<Schedule>> groupSchedulesByMachineType(List<Schedule> schedules) {
        return schedules.stream()
                .filter(schedule -> schedule.getMachineType() != null)
                .collect(Collectors.groupingBy(Schedule::getMachineType));
    }

    private void saveSchedulesToFile(List<Schedule> schedules, String type) {
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> modelMapperExtended.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
        String fileName = "./data/job-scheduled-by-" + type + ".json";

        try {
            new File("./data").mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), scheduleDTOs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}