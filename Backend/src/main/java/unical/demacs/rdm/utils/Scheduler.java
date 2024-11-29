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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

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

    private List<ScheduleDTO> getSchedulesDueBefore(LocalDateTime date) {
        List<Schedule> schedules = scheduleServiceImpl.getSchedulesDueAfter(date);
        return schedules.stream()
                .map(schedule -> modelMapperExtended.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
    }

    // Method to save a list of Schedules in JSON format to a file
    public void saveSchedulesToFile(List<Schedule> schedules, String type) {
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> modelMapperExtended.map(schedule, ScheduleDTO.class))
                .collect(Collectors.toList());
        String fileName = "./data/jobScheduled_" + type + ".json";

        try {
            // Ensure the directory exists
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
        // Get all pending jobs
        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);

        // Group jobs by required machine type
        Map<MachineType, List<Job>> jobsByMachineType = pendingJobs.stream()
                .collect(Collectors.groupingBy(Job::getRequiredMachineType));

        // For each machine type
        for (Map.Entry<MachineType, List<Job>> entry : jobsByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Job> jobsForMachineType = entry.getValue();

            // Get all machines of this type
            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());

            if (machines.isEmpty()) {
                // No machines available for this type; cannot schedule these jobs
                continue;
            }

            // Sort jobs by priority (higher priority first), then by arrival time (using id)
            jobsForMachineType.sort(Comparator
                    .comparing((Job job) -> job.getPriority().ordinal()).reversed()
                    .thenComparing(Job::getId));

            // Initialize machine availability map
            Map<Machine, LocalDateTime> machineAvailability = new HashMap<>();
            for (Machine machine : machines) {
                // Initialize availability to current time
                machineAvailability.put(machine, LocalDateTime.now());
            }

            // Assign jobs to machines
            for (Job job : jobsForMachineType) {
                // Find the machine that becomes available the earliest
                Machine earliestAvailableMachine = machines.get(0);
                LocalDateTime earliestAvailableTime = machineAvailability.get(earliestAvailableMachine);

                for (Machine machine : machines) {
                    LocalDateTime availableTime = machineAvailability.get(machine);
                    if (availableTime.isBefore(earliestAvailableTime)) {
                        earliestAvailableTime = availableTime;
                        earliestAvailableMachine = machine;
                    }
                }

                // Schedule the job on the earliest available machine
                Schedule schedule = Schedule.scheduleBuilder()
                        .job(job)
                        .machineType(machineType)
                        .startTime(earliestAvailableTime)
                        .duration(job.getDuration())
                        .status(ScheduleStatus.SCHEDULED)
                        .build();

                scheduleRepository.save(schedule);

                // Update machine availability
                LocalDateTime jobEndTime = earliestAvailableTime.plusMinutes(job.getDuration());
                machineAvailability.put(earliestAvailableMachine, jobEndTime);

                // Update job status to SCHEDULED
                job.setStatus(JobStatus.SCHEDULED);
                jobRepository.save(job);
            }
        }

        // After scheduling, retrieve the schedules and save to file
        List<Schedule> schedules = scheduleRepository.findAll();
        saveSchedulesToFile(schedules, "priority");
    }

    public void testSchedulingByDueDate() {
        // Get all pending jobs
        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);

        // Assign due dates to jobs (for testing purposes)
        Map<Job, LocalDateTime> jobDueDates = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < pendingJobs.size(); i++) {
            Job job = pendingJobs.get(i);
            // Assign due date as now plus i days
            LocalDateTime dueDate = now.plusDays(i);
            jobDueDates.put(job, dueDate);
        }

        // Group jobs by required machine type
        Map<MachineType, List<Job>> jobsByMachineType = pendingJobs.stream()
                .collect(Collectors.groupingBy(Job::getRequiredMachineType));

        // For each machine type
        for (Map.Entry<MachineType, List<Job>> entry : jobsByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Job> jobsForMachineType = entry.getValue();

            // Get all machines of this type
            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());

            if (machines.isEmpty()) {
                // No machines available for this type; cannot schedule these jobs
                continue;
            }

            // Sort jobs by due date (earlier due date first), then by arrival time (using id)
            jobsForMachineType.sort(
                    Comparator.comparing((Job job) -> jobDueDates.get(job))
                            .thenComparing(Job::getId)
            );

            // Initialize machine availability map
            Map<Machine, LocalDateTime> machineAvailability = new HashMap<>();
            for (Machine machine : machines) {
                // Initialize availability to current time
                machineAvailability.put(machine, LocalDateTime.now());
            }

            // Assign jobs to machines
            for (Job job : jobsForMachineType) {
                // Find the machine that becomes available the earliest
                Machine earliestAvailableMachine = machines.get(0);
                LocalDateTime earliestAvailableTime = machineAvailability.get(earliestAvailableMachine);

                for (Machine machine : machines) {
                    LocalDateTime availableTime = machineAvailability.get(machine);
                    if (availableTime.isBefore(earliestAvailableTime)) {
                        earliestAvailableTime = availableTime;
                        earliestAvailableMachine = machine;
                    }
                }

                // Schedule the job on the earliest available machine
                Schedule schedule = Schedule.scheduleBuilder()
                        .job(job)
                        .machineType(machineType)
                        .startTime(earliestAvailableTime)
                        .duration(job.getDuration())
                        .dueDate(jobDueDates.get(job))
                        .status(ScheduleStatus.SCHEDULED)
                        .build();

                scheduleRepository.save(schedule);

                // Update machine availability
                LocalDateTime jobEndTime = earliestAvailableTime.plusMinutes(job.getDuration());
                machineAvailability.put(earliestAvailableMachine, jobEndTime);

                // Update job status to SCHEDULED
                job.setStatus(JobStatus.SCHEDULED);
                jobRepository.save(job);
            }
        }

        // After scheduling, retrieve the schedules and save to file
        List<Schedule> schedules = scheduleRepository.findAll();
        saveSchedulesToFile(schedules, "dueDate");
    }
}
