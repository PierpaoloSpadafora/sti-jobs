package unical.demacs.rdm.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.*;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.dto.ScheduleWithMachineDTO;
import unical.demacs.rdm.persistence.entities.*;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.*;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class Scheduler {
    private final ScheduleRepository scheduleRepository;
    private final MachineRepository machineRepository;
    private final ModelMapperExtended modelMapperExtended;
    private final ObjectMapper objectMapper;

    public void scheduleByEveryType() {
        scheduleByPriority();
        scheduleByDueDate();
        scheduleByDuration();
    }

    public void scheduleByPriority() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<Schedule> priorityResult = scheduleWithOptaPlanner(schedules, "priority");
        saveSchedulesToFile(priorityResult, "priority");
    }



    public void scheduleByDueDate() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<Schedule> dueDateResult = scheduleWithOptaPlanner(schedules, "due-date");
        saveSchedulesToFile(dueDateResult, "due-date");
    }

    public void scheduleByDuration() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<Schedule> durationResult = scheduleWithOptaPlanner(schedules, "duration");
        saveSchedulesToFile(durationResult, "duration");
    }

    // -------------------- SCHEDULE LOGIC 'NAGG RO' CAZZ --------------------

    private List<JobAssignment> createPossibleAssignments(List<Schedule> schedules, List<Machine> machines) {
        List<JobAssignment> assignments = new ArrayList<>();
        log.info("Creating assignments for {} schedules and {} machines", schedules.size(), machines.size());

        Map<Long, List<Machine>> machinesByType = machines.stream()
                .collect(Collectors.groupingBy(m -> m.getMachine_type_id().getId()));

        for (Schedule schedule : schedules) {
            Long requiredTypeId = schedule.getMachineType().getId();
            List<Machine> compatibleMachines = machinesByType.getOrDefault(requiredTypeId, Collections.emptyList());

            if (compatibleMachines.isEmpty()) {
                log.warn("No compatible machines found for schedule {} (type {})", schedule.getId(), requiredTypeId);
                continue;
            }

            for (Machine machine : compatibleMachines) {
                JobAssignment assignment = new JobAssignment(schedule);
                assignment.setAssignedMachine(machine);
                assignments.add(assignment);
                log.debug("Created assignment: Schedule {} -> Machine {}", schedule.getId(), machine.getId());
            }
        }

        return assignments;
    }

    private TimeWindow calculateTimeWindow(List<Schedule> schedules) {
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("No schedules provided for time window calculation");
        }

        LocalDateTime earliestStart = schedules.stream()
                .map(Schedule::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElseGet(LocalDateTime::now);

        LocalDateTime latestDue = schedules.stream()
                .map(Schedule::getDueDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElseGet(() -> earliestStart.plusDays(7));

        long startTime = earliestStart.toEpochSecond(ZoneOffset.UTC);
        long endTime = latestDue.toEpochSecond(ZoneOffset.UTC);

        log.info("Time window: {} to {}",
                earliestStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                latestDue.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return new TimeWindow(startTime, endTime);
    }

    private List<TimeGrain> createTimeGrains(TimeWindow timeWindow) {
        List<TimeGrain> timeGrains = new ArrayList<>();
        int grainIndex = 0;
        long currentTime = timeWindow.getStartTime();
        int grainLengthInSeconds = 60;

        while (currentTime <= timeWindow.getEndTime()) {
            timeGrains.add(new TimeGrain(grainIndex++, currentTime));
            currentTime += grainLengthInSeconds;
        }

        log.info("Created {} time grains", timeGrains.size());
        return timeGrains;
    }

    private List<Schedule> processSolution(ScheduleSolution solution) {
        if (solution == null || solution.getScore() == null) {
            log.error("Invalid solution or score");
            return Collections.emptyList();
        }

        log.info("Processing solution with score: {}", solution.getScore());

        if (!solution.getScore().isFeasible()) {
            log.warn("Solution is not feasible!");
        }

        Map<Long, Schedule> finalSchedules = new HashMap<>();

        for (JobAssignment assignment : solution.getJobAssignments()) {
            if (assignment.getAssignedMachine() != null && assignment.getStartTimeGrain() != null) {
                Schedule schedule = assignment.getSchedule();
                Long scheduleId = schedule.getId();

                if (!finalSchedules.containsKey(scheduleId)) {
                    LocalDateTime startTime = LocalDateTime.ofEpochSecond(
                            assignment.getStartTimeGrain().getStartTimeInSeconds(),
                            0,
                            ZoneOffset.UTC
                    );

                    schedule.setStartTime(startTime);
                    schedule.setMachineType(assignment.getAssignedMachine().getMachine_type_id());
                    schedule.setMachine(assignment.getAssignedMachine());
                    schedule.setStatus(ScheduleStatus.SCHEDULED);
                    finalSchedules.put(scheduleId, schedule);

                    log.info("Scheduled: {} on machine {} at {}",
                            scheduleId, assignment.getAssignedMachine().getId(), startTime);
                }
            } else {
                log.warn("Job {} not fully assigned", assignment.getSchedule().getId());
            }
        }

        return new ArrayList<>(finalSchedules.values());
    }

    private ScheduleConstraintConfiguration configureConstraints(String criterion) {
        ScheduleConstraintConfiguration config = new ScheduleConstraintConfiguration();

        switch (criterion) {
            case "priority":
                config.setHighPriorityJobsFirst(HardSoftScore.ofSoft(1000));
                config.setJobDueDate(HardSoftScore.ofHard(1));
                config.setMachineConflict(HardSoftScore.ofHard(1));
                config.setShortDurationJobsFirst(HardSoftScore.ofSoft(1));
                config.setBalanceMachineLoad(HardSoftScore.ofSoft(500));
                break;
            case "due-date":
                config.setJobDueDate(HardSoftScore.ofHard(1000));
                config.setHighPriorityJobsFirst(HardSoftScore.ofSoft(1));
                config.setMachineConflict(HardSoftScore.ofHard(1));
                config.setShortDurationJobsFirst(HardSoftScore.ofSoft(1));
                config.setBalanceMachineLoad(HardSoftScore.ofSoft(500));
                break;
            case "duration":
                config.setShortDurationJobsFirst(HardSoftScore.ofSoft(1000));
                config.setHighPriorityJobsFirst(HardSoftScore.ofSoft(1));
                config.setJobDueDate(HardSoftScore.ofHard(1));
                config.setMachineConflict(HardSoftScore.ofHard(1));
                config.setBalanceMachineLoad(HardSoftScore.ofSoft(500));
                break;
            default:
                throw new IllegalArgumentException("Unknown scheduling criterion: " + criterion);
        }

        return config;
    }

    private List<Schedule> scheduleWithOptaPlanner(List<Schedule> schedules, String criterion) {
        log.info("Starting scheduling process for criterion: {}", criterion);

        List<Schedule> validSchedules = schedules.stream()
                .filter(s -> s.getStartTime() != null)
                .filter(s -> s.getStatus() != ScheduleStatus.COMPLETED)
                .collect(Collectors.toList());

        if (validSchedules.isEmpty()) {
            log.warn("No valid schedules to process");
            return Collections.emptyList();
        }

        List<Machine> availableMachines = machineRepository.findAll().stream()
                .filter(m -> !(m.getStatus().equals(MachineStatus.BUSY)))
                .collect(Collectors.toList());

        if (availableMachines.isEmpty()) {
            log.error("No available machines found");
            return Collections.emptyList();
        }

        List<JobAssignment> jobAssignments = createPossibleAssignments(validSchedules, availableMachines);
        TimeWindow timeWindow = calculateTimeWindow(validSchedules);
        List<TimeGrain> timeGrainRange = createTimeGrains(timeWindow);
        ScheduleConstraintConfiguration constraintConfiguration = configureConstraints(criterion);

        ScheduleSolution solution = createAndSolveProblem(
                jobAssignments,
                availableMachines,
                timeGrainRange,
                constraintConfiguration
        );

        return processSolution(solution);
    }

    private SolverConfig createSolverConfig() {
        return new SolverConfig()
                .withSolutionClass(ScheduleSolution.class)
                .withEntityClasses(JobAssignment.class)
                .withConstraintProviderClass(ScheduleConstraintProvider.class)
                .withTerminationConfig(new TerminationConfig()
                        .withBestScoreFeasible(true)
                        .withSecondsSpentLimit(30L));
    }

    private ScheduleSolution createAndSolveProblem(
            List<JobAssignment> jobAssignments,
            List<Machine> machines,
            List<TimeGrain> timeGrainRange,
            ScheduleConstraintConfiguration constraintConfiguration) {

        for(JobAssignment ja : jobAssignments){
            System.out.println(ja.toString());
        }

        System.out.println("\n=== Starting Scheduling Process ===");
        System.out.printf("Jobs to schedule: %d%n", jobAssignments.size());
        System.out.printf("Available machines: %d%n", machines.size());
        System.out.printf("Time grains: %d%n", timeGrainRange.size());

        ScheduleSolution problem = new ScheduleSolution(
                jobAssignments,
                machines,
                timeGrainRange,
                constraintConfiguration
        );

        SolverFactory<ScheduleSolution> solverFactory = SolverFactory.create(createSolverConfig());
        Solver<ScheduleSolution> solver = solverFactory.buildSolver();

        try {
            System.out.println("\nSolver starting...");
            ScheduleSolution solution = solver.solve(problem);
            System.out.printf("\nSolver finished with score: %s%n", solution.getScore());
            return solution;
        } catch (Exception e) {
            System.err.println("Solver failed with error: " + e.getMessage());
            throw new RuntimeException("Failed to solve scheduling problem", e);
        }
    }

    private void saveSchedulesToFile(List<Schedule> schedules, String type) {
        if (schedules.isEmpty()) {
            log.warn("No schedules to save for type: {}", type);
            return;
        }

        List<ScheduleWithMachineDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> {
                    ScheduleWithMachineDTO dto = modelMapperExtended.map(schedule, ScheduleWithMachineDTO.class);
                    dto.setJobId(schedule.getJob().getId());
                    dto.setMachineTypeId(schedule.getMachineType().getId());
                    if (schedule.getMachine() != null) {
                        dto.setMachineId(schedule.getMachine().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        String fileName = "./data/job-scheduled-by-" + type + ".json";
        File dataDir = new File("./data");

        if (!dataDir.exists() && !dataDir.mkdirs()) {
            log.error("Failed to create directory: {}", dataDir.getAbsolutePath());
            throw new RuntimeException("Unable to create data directory");
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), scheduleDTOs);
            log.info("Successfully saved {} schedules to {}", schedules.size(), fileName);
        } catch (IOException e) {
            log.error("Failed to write schedule to file: {}", fileName, e);
            throw new RuntimeException("Failed to save schedule to file", e);
        }
    }
}