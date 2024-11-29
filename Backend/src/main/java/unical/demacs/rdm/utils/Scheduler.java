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
import unical.demacs.rdm.persistence.service.implementation.ScheduleServiceImpl;

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
    private final ScheduleServiceImpl scheduleServiceImpl;
    private final MachineRepository machineRepository;
    private final ObjectMapper objectMapper;

    //---------------------- WORK IN PROGRESS ---------------------------------------

    public void saveSchedulesToFile(List<Schedule> schedules, String type) {
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> modelMapperExtended.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
        String fileName = "./data/jobScheduled_" + type + ".json";

        try {
            new File("./data").mkdirs();
            objectMapper.writeValue(new File(fileName), scheduleDTOs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scheduleTesting(String type) {
        if (type.equals("priority")) {
            testSchedulingByPriority();
        } else if (type.equals("dueDate")) {
            testSchedulingByDueDate();
        }
    }

    /*
    Each schedule has a job, a machineType, a startTime, a duration (end time is implicit),
    a due date, a priority, and a status.
    We can have multiple machines of the same type. If we have multiple jobs with the same
    machine type but multiple machines of the same type, those jobs can be executed in parallel.

    - If we have multiple jobs with the same machine type and the same priority,
      the job that arrives first is executed first.
    - If we have multiple jobs with the same machine type and different priorities,
      the job with higher priority is executed first.
    - If we have multiple jobs with the same machine type and equal priority,
      the job that arrives first is executed first.
    - If we have multiple jobs with the same machine type, equal priority, and the same due date,
      the job that arrives first is executed first.
    */
    public void testSchedulingByPriority() {
        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);
        Map<MachineType, List<Job>> jobsByMachineType = pendingJobs.stream()
                .collect(Collectors.groupingBy(Job::getRequiredMachineType));
        for (Map.Entry<MachineType, List<Job>> entry : jobsByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Job> jobsForMachineType = entry.getValue();
            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }
            jobsForMachineType.sort(Comparator
                    .comparing((Job job) -> job.getPriority().ordinal()).reversed()
                    .thenComparing(Job::getId));
            Map<Machine, LocalDateTime> machineAvailability = new HashMap<>();
            for (Machine machine : machines) {
                machineAvailability.put(machine, LocalDateTime.now());
            }
            for (Job job : jobsForMachineType) {
                Machine earliestAvailableMachine = machines.get(0);
                LocalDateTime earliestAvailableTime = machineAvailability.get(earliestAvailableMachine);
                for (Machine machine : machines) {
                    LocalDateTime availableTime = machineAvailability.get(machine);
                    if (availableTime.isBefore(earliestAvailableTime)) {
                        earliestAvailableTime = availableTime;
                        earliestAvailableMachine = machine;
                    }
                }
                Schedule schedule = Schedule.scheduleBuilder()
                        .job(job)
                        .machineType(machineType)
                        .startTime(earliestAvailableTime)
                        .duration(job.getDuration())
                        .status(ScheduleStatus.SCHEDULED)
                        .build();
                scheduleRepository.save(schedule);
                LocalDateTime jobEndTime = earliestAvailableTime.plusMinutes(job.getDuration());
                machineAvailability.put(earliestAvailableMachine, jobEndTime);
                job.setStatus(JobStatus.SCHEDULED);
                jobRepository.save(job);
            }
        }
        List<Schedule> schedules = scheduleRepository.findAll();
        saveSchedulesToFile(schedules, "priority");
    }

    public void testSchedulingByDueDate() {
        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);

        Map<Job, LocalDateTime> jobDueDates = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < pendingJobs.size(); i++) {
            Job job = pendingJobs.get(i);
            LocalDateTime dueDate = now.plusDays(i);
            jobDueDates.put(job, dueDate);
        }
        Map<MachineType, List<Job>> jobsByMachineType = pendingJobs.stream()
                .collect(Collectors.groupingBy(Job::getRequiredMachineType));

        for (Map.Entry<MachineType, List<Job>> entry : jobsByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Job> jobsForMachineType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());

            if (machines.isEmpty()) {
                continue;
            }

            jobsForMachineType.sort(
                    Comparator.comparing((Job job) -> jobDueDates.get(job))
                            .thenComparing(Job::getId)
            );

            Map<Machine, LocalDateTime> machineAvailability = new HashMap<>();
            for (Machine machine : machines) {
                machineAvailability.put(machine, LocalDateTime.now());
            }

            for (Job job : jobsForMachineType) {
                Machine earliestAvailableMachine = machines.get(0);
                LocalDateTime earliestAvailableTime = machineAvailability.get(earliestAvailableMachine);

                for (Machine machine : machines) {
                    LocalDateTime availableTime = machineAvailability.get(machine);
                    if (availableTime.isBefore(earliestAvailableTime)) {
                        earliestAvailableTime = availableTime;
                        earliestAvailableMachine = machine;
                    }
                }

                Schedule schedule = Schedule.scheduleBuilder()
                        .job(job)
                        .machineType(machineType)
                        .startTime(earliestAvailableTime)
                        .duration(job.getDuration())
                        .dueDate(jobDueDates.get(job))
                        .status(ScheduleStatus.SCHEDULED)
                        .build();

                scheduleRepository.save(schedule);

                LocalDateTime jobEndTime = earliestAvailableTime.plusMinutes(job.getDuration());
                machineAvailability.put(earliestAvailableMachine, jobEndTime);

                job.setStatus(JobStatus.SCHEDULED);
                jobRepository.save(job);
            }
        }

        List<Schedule> schedules = scheduleRepository.findAll();
        saveSchedulesToFile(schedules, "dueDate");
    }
}
