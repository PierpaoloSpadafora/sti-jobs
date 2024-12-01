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

        LocalDateTime now = LocalDateTime.now();
        Map<Long, Job> jobsMap = loadJobsMap(schedules);
        Map<MachineType, List<Schedule>> schedulesByMachineType = groupSchedulesByMachineType(schedules);

        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Schedule> schedulesForType = entry.getValue();

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

            Map<Machine, LocalDateTime> machineAvailability = initializeMachineAvailabilityConsideringCurrentJobs(machines, schedulesForType, now);
            updateSchedulesConsideringCurrentTime(schedulesForType, machines, machineAvailability, jobsMap, now);
        }

        saveSchedulesToFile(schedules, "priority");
    }


    private void scheduleByDueDate() {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return;
        }

        Map<Long, Job> jobsMap = loadJobsMap(schedules);
        Map<MachineType, List<Schedule>> schedulesByMachineType = groupSchedulesByMachineType(schedules);

        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Schedule> schedulesForType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }

            schedulesForType.sort(Comparator
                    .comparing(Schedule::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(schedule -> schedule.getJob().getId()));

            Map<Machine, LocalDateTime> machineAvailability = initializeMachineAvailability(machines);
            updateSchedules(schedulesForType, machines, machineAvailability, jobsMap);
        }

        saveSchedulesToFile(schedules, "due-date");
    }

    private void scheduleByDuration() {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return;
        }

        Map<Long, Job> jobsMap = loadJobsMap(schedules);
        Map<MachineType, List<Schedule>> schedulesByMachineType = groupSchedulesByMachineType(schedules);

        for (Map.Entry<MachineType, List<Schedule>> entry : schedulesByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Schedule> schedulesForType = entry.getValue();

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


            Map<Machine, LocalDateTime> machineAvailability = initializeMachineAvailability(machines);
            updateSchedules(schedulesForType, machines, machineAvailability, jobsMap);
        }

        saveSchedulesToFile(schedules, "duration");
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

    private Map<Machine, LocalDateTime> initializeMachineAvailability(List<Machine> machines) {
        LocalDateTime now = LocalDateTime.now();
        return machines.stream()
                .collect(Collectors.toMap(machine -> machine, machine -> now));
    }

    private void updateSchedules(List<Schedule> schedules, List<Machine> machines,
                                 Map<Machine, LocalDateTime> machineAvailability, Map<Long, Job> jobsMap) {
        for (Schedule schedule : schedules) {
            Machine earliestMachine = findEarliestAvailableMachine(machines, machineAvailability);
            LocalDateTime startTime = machineAvailability.get(earliestMachine);
            Job job = jobsMap.get(schedule.getJob().getId());

            if (job != null) {
                schedule.setStartTime(startTime);
                schedule.setStatus(ScheduleStatus.SCHEDULED);
                scheduleRepository.save(schedule);

                updateMachineAvailability(machineAvailability, earliestMachine, startTime, job.getDuration());
                updateJobStatus(job, JobStatus.SCHEDULED);
            }
        }
    }

    private Machine findEarliestAvailableMachine(List<Machine> machines, Map<Machine, LocalDateTime> machineAvailability) {
        return machines.stream()
                .min(Comparator.comparing(machineAvailability::get))
                .orElseThrow(() -> new NoSuchElementException("No machines available"));
    }

    private void updateMachineAvailability(Map<Machine, LocalDateTime> machineAvailability,
                                           Machine machine, LocalDateTime startTime, Long duration) {
        LocalDateTime jobEndTime = startTime.plusSeconds(duration);
        machineAvailability.put(machine, jobEndTime);
    }

    private void updateJobStatus(Job job, JobStatus status) {
        job.setStatus(status);
        jobRepository.save(job);
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

    private Map<Machine, LocalDateTime> initializeMachineAvailabilityConsideringCurrentJobs(List<Machine> machines, List<Schedule> schedules, LocalDateTime now) {
        Map<Machine, LocalDateTime> machineAvailability = initializeMachineAvailability(machines);

        for (Schedule schedule : schedules) {
            Machine machine = schedule.getMachineType().getMachines().stream()
                    .filter(machines::contains)
                    .findFirst()
                    .orElse(null);
            if (machine != null && schedule.getStartTime() != null) {
                LocalDateTime jobEndTime = schedule.getStartTime().plusSeconds(schedule.getJob().getDuration());
                if (jobEndTime.isAfter(now)) {
                    machineAvailability.put(machine, jobEndTime);
                }
            }
        }
        return machineAvailability;
    }

    private void updateSchedulesConsideringCurrentTime(List<Schedule> schedules, List<Machine> machines,
                                                       Map<Machine, LocalDateTime> machineAvailability, Map<Long, Job> jobsMap, LocalDateTime now) {
        for (Schedule schedule : schedules) {
            Machine earliestMachine = findEarliestAvailableMachine(machines, machineAvailability);
            LocalDateTime startTime = machineAvailability.get(earliestMachine);

            if (startTime.isBefore(now)) {
                continue; // Skip jobs that are already scheduled and should be running
            }

            Job job = jobsMap.get(schedule.getJob().getId());
            if (job != null) {
                schedule.setStartTime(startTime);
                schedule.setStatus(ScheduleStatus.SCHEDULED);
                scheduleRepository.save(schedule);

                updateMachineAvailability(machineAvailability, earliestMachine, startTime, job.getDuration());
                updateJobStatus(job, JobStatus.SCHEDULED);
            }
        }
    }


}
