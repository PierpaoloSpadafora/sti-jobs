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
import unical.demacs.rdm.persistence.enums.JobStatus;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.JobRepository;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;

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
    private final JobRepository jobRepository;
    private final MachineRepository machineRepository;
    private final ObjectMapper objectMapper;

    public void scheduleByEveryType() {
        scheduleByPriority();
        scheduleByDueDate();
        scheduleByDuration();
    }

    private void scheduleByPriority() {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return;
        }

        Map<Long, Job> jobsMap = loadJobsMap(schedules);
        Map<MachineType, List<Schedule>> schedulesByMachineType = groupSchedulesByMachineType(schedules);

        List<Schedule> schedulesForType = new ArrayList<>();
        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            schedulesForType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }

            schedulesForType.sort(new Comparator<Schedule>() {
                @Override
                public int compare(Schedule schedule1, Schedule schedule2) {
                    Job job1 = jobsMap.get(schedule1.getJob().getId());
                    Job job2 = jobsMap.get(schedule2.getJob().getId());

                    int priority1 = (job1 != null) ? job1.getPriority().ordinal() : Integer.MAX_VALUE;
                    int priority2 = (job2 != null) ? job2.getPriority().ordinal() : Integer.MAX_VALUE;

                    int priorityComparison = Integer.compare(priority2, priority1);
                    if (priorityComparison != 0) {
                        return priorityComparison;
                    }

                    Long jobId1 = schedule1.getJob().getId();
                    Long jobId2 = schedule2.getJob().getId();
                    return jobId1.compareTo(jobId2);
                }
            });
        }
        saveSchedulesToFile(schedulesForType, "priority");
    }


    private void scheduleByDueDate() {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return;
        }

        Map<Long, Job> jobsMap = loadJobsMap(schedules);
        Map<MachineType, List<Schedule>> schedulesByMachineType = groupSchedulesByMachineType(schedules);

        List<Schedule> schedulesForType = new ArrayList<>();
        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            schedulesForType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }

            schedulesForType.sort(Comparator
                    .comparing(Schedule::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(schedule -> schedule.getJob().getId()));

        }

        saveSchedulesToFile(schedulesForType, "due-date");
    }

    private void scheduleByDuration() {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return;
        }

        Map<Long, Job> jobsMap = loadJobsMap(schedules);
        Map<MachineType, List<Schedule>> schedulesByMachineType = groupSchedulesByMachineType(schedules);

        List<Schedule> schedulesForType = new ArrayList<>();
        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            schedulesForType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }
            schedulesForType.sort(new Comparator<Schedule>() {
                @Override
                public int compare(Schedule schedule1, Schedule schedule2) {
                    Job job1 = jobsMap.get(schedule1.getJob().getId());
                    Job job2 = jobsMap.get(schedule2.getJob().getId());

                    long duration1 = (job1 != null) ? job1.getDuration() : Long.MAX_VALUE;
                    long duration2 = (job2 != null) ? job2.getDuration() : Long.MAX_VALUE;

                    int durationComparison = Long.compare(duration1, duration2);
                    if (durationComparison != 0) {
                        return durationComparison;
                    }

                    Long jobId1 = schedule1.getJob().getId();
                    Long jobId2 = schedule2.getJob().getId();
                    return jobId1.compareTo(jobId2);
                }
            });

        }

        saveSchedulesToFile(schedulesForType, "duration");
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
