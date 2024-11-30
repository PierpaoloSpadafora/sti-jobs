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

    public void scheduleByEveryType(){
        scheduleByPriority();
        scheduleByDueDate();
        scheduleByDuration();
    }

    private void scheduleByPriority() {
        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);
        Map<MachineType, List<Job>> jobsByMachineType = groupJobsByMachineType(pendingJobs);

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

            Map<Machine, LocalDateTime> machineAvailability = initializeMachineAvailability(machines);
            scheduleJobs(jobsForMachineType, machines, machineAvailability, machineType);
        }

        saveSchedulesToFile(scheduleRepository.findAll(), "priority");
    }

    private void scheduleByDueDate() {
        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);
        Map<Job, LocalDateTime> jobDueDates = assignDueDates(pendingJobs);

        Map<MachineType, List<Job>> jobsByMachineType = groupJobsByMachineType(pendingJobs);

        for (Map.Entry<MachineType, List<Job>> entry : jobsByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Job> jobsForMachineType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }

            jobsForMachineType.sort(
                    Comparator.comparing((Job job) -> jobDueDates.get(job))
                            .thenComparing(Job::getId));

            Map<Machine, LocalDateTime> machineAvailability = initializeMachineAvailability(machines);
            scheduleJobsWithDueDate(jobsForMachineType, machines, machineAvailability, machineType, jobDueDates);
        }

        saveSchedulesToFile(scheduleRepository.findAll(), "due-date");
    }

    public void scheduleByDuration() {
        List<Job> pendingJobs = jobRepository.findByStatus(JobStatus.PENDING);
        Map<MachineType, List<Job>> jobsByMachineType = groupJobsByMachineType(pendingJobs);

        for (Map.Entry<MachineType, List<Job>> entry : jobsByMachineType.entrySet()) {
            MachineType machineType = entry.getKey();
            List<Job> jobsForMachineType = entry.getValue();

            List<Machine> machines = machineRepository.findByTypeId(machineType.getId());
            if (machines.isEmpty()) {
                continue;
            }

            jobsForMachineType.sort(
                    Comparator.comparing(Job::getDuration)
                            .thenComparing(Job::getId)
            );

            Map<Machine, LocalDateTime> machineAvailability = initializeMachineAvailability(machines);
            scheduleJobs(jobsForMachineType, machines, machineAvailability, machineType);
        }

        saveSchedulesToFile(scheduleRepository.findAll(), "duration");
    }


    private Map<MachineType, List<Job>> groupJobsByMachineType(List<Job> jobs) {
        return jobs.stream()
                .collect(Collectors.groupingBy(Job::getRequiredMachineType));
    }

    private Map<Machine, LocalDateTime> initializeMachineAvailability(List<Machine> machines) {
        Map<Machine, LocalDateTime> machineAvailability = new HashMap<>();
        for (Machine machine : machines) {
            machineAvailability.put(machine, LocalDateTime.now());
        }
        return machineAvailability;
    }

    private void scheduleJobs(List<Job> jobs, List<Machine> machines,
                              Map<Machine, LocalDateTime> machineAvailability, MachineType machineType) {
        for (Job job : jobs) {
            Machine earliestMachine = findEarliestAvailableMachine(machines, machineAvailability);
            LocalDateTime startTime = machineAvailability.get(earliestMachine);

            Schedule schedule = createSchedule(job, machineType, startTime, null);
            scheduleRepository.save(schedule);

            updateMachineAvailability(machineAvailability, earliestMachine, startTime, job.getDuration());
            updateJobStatus(job, JobStatus.SCHEDULED);
        }
    }

    private void scheduleJobsWithDueDate(List<Job> jobs, List<Machine> machines,
                                         Map<Machine, LocalDateTime> machineAvailability, MachineType machineType,
                                         Map<Job, LocalDateTime> jobDueDates) {
        for (Job job : jobs) {
            Machine earliestMachine = findEarliestAvailableMachine(machines, machineAvailability);
            LocalDateTime startTime = machineAvailability.get(earliestMachine);

            Schedule schedule = createSchedule(job, machineType, startTime, jobDueDates.get(job));
            scheduleRepository.save(schedule);

            updateMachineAvailability(machineAvailability, earliestMachine, startTime, job.getDuration());
            updateJobStatus(job, JobStatus.SCHEDULED);
        }
    }

    private Machine findEarliestAvailableMachine(List<Machine> machines, Map<Machine, LocalDateTime> machineAvailability) {
        return machines.stream()
                .min(Comparator.comparing(machineAvailability::get))
                .orElseThrow(() -> new NoSuchElementException("No machines available"));
    }

    private Schedule createSchedule(Job job, MachineType machineType, LocalDateTime startTime, LocalDateTime dueDate) {
        return Schedule.scheduleBuilder()
                .job(job)
                .machineType(machineType)
                .startTime(startTime)
                .duration(job.getDuration())
                .dueDate(dueDate)
                .status(ScheduleStatus.SCHEDULED)
                .build();
    }

    private void updateMachineAvailability(Map<Machine, LocalDateTime> machineAvailability,
                                           Machine machine, LocalDateTime startTime, Long duration) {
        LocalDateTime jobEndTime = startTime.plusMinutes(duration);
        machineAvailability.put(machine, jobEndTime);
    }

    private void updateJobStatus(Job job, JobStatus status) {
        job.setStatus(status);
        jobRepository.save(job);
    }

    private Map<Job, LocalDateTime> assignDueDates(List<Job> jobs) {
        Map<Job, LocalDateTime> jobDueDates = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < jobs.size(); i++) {
            jobDueDates.put(jobs.get(i), now.plusDays(i));
        }
        return jobDueDates;
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
