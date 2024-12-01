package unical.demacs.rdm.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.ScheduleDTO;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.MachineType;
import unical.demacs.rdm.persistence.entities.Schedule;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Scheduler {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapperExtended modelMapperExtended;
    private final JobRepository jobRepository;
    private final MachineRepository machineRepository;
    private final ObjectMapper objectMapper;

    public void scheduleByEveryType() {
        scheduleByPriority();
        scheduleByDueDate();
        scheduleByDuration();
    }

    private void scheduleByPriority() {
        scheduleBy("priority", (s1, s2) -> {
            int priority1 = (s1.getJob() != null && s1.getJob().getPriority() != null) ? s1.getJob().getPriority().ordinal() : Integer.MAX_VALUE;
            int priority2 = (s2.getJob() != null && s2.getJob().getPriority() != null) ? s2.getJob().getPriority().ordinal() : Integer.MAX_VALUE;

            int priorityComparison = Integer.compare(priority2, priority1);
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            Long jobId1 = s1.getJob().getId();
            Long jobId2 = s2.getJob().getId();
            return jobId1.compareTo(jobId2);
        }, true);
    }

    private void scheduleByDueDate() {
        scheduleBy("due-date", Comparator
                .comparing(Schedule::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(schedule -> schedule.getJob().getId()), false);
    }

    private void scheduleByDuration() {
        scheduleBy("duration", (s1, s2) -> {
            long duration1 = (s1.getJob() != null) ? s1.getJob().getDuration() : Long.MAX_VALUE;
            long duration2 = (s2.getJob() != null) ? s2.getJob().getDuration() : Long.MAX_VALUE;

            int durationComparison = Long.compare(duration1, duration2);
            if (durationComparison != 0) {
                return durationComparison;
            }

            Long jobId1 = s1.getJob().getId();
            Long jobId2 = s2.getJob().getId();
            return jobId1.compareTo(jobId2);
        }, true);
    }

    private void scheduleBy(String type, Comparator<Schedule> comparator, boolean requiresJobDetails) {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return;
        }

        Map<Long, Job> jobsMap = requiresJobDetails ? loadJobsMap(schedules) : null;
        if (jobsMap != null) {
            for (Schedule schedule : schedules) {
                Job job = jobsMap.get(schedule.getJob().getId());
                schedule.setJob(job);
            }
        }

        Map<MachineType, List<Schedule>> schedulesByMachineType = groupSchedulesByMachineType(schedules);

        List<Schedule> allSortedSchedules = new ArrayList<>();
        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Schedule> schedulesForType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }

            schedulesForType.sort(comparator);
            allSortedSchedules.addAll(schedulesForType);
        }

        saveSchedulesToFile(allSortedSchedules, type);
    }

    private Map<Long, Job> loadJobsMap(List<Schedule> schedules) {
        List<Long> jobIds = schedules.stream()
                .map(schedule -> schedule.getJob().getId())
                .distinct()
                .collect(Collectors.toList());
        List<Job> jobs = jobRepository.findAllById(jobIds);
        return jobs.stream().collect(Collectors.toMap(Job::getId, job -> job));
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
