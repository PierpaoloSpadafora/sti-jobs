package unical.demacs.rdm.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import unical.demacs.rdm.config.ModelMapperExtended;
import unical.demacs.rdm.persistence.entities.*;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;
import unical.demacs.rdm.persistence.repository.MachineRepository;
import unical.demacs.rdm.persistence.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SchedulerTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MachineRepository machineRepository;

    private Scheduler scheduler;
    private Map<Long, MachineType> machineTypes;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        machineTypes = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            MachineType type = new MachineType();
            type.setId((long) i);
            type.setName("Type " + i);
            machineTypes.put((long) i, type);
        }

        List<Machine> testMachines = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Machine machine = new Machine();
            machine.setId((long) i);
            machine.setMachine_type_id(machineTypes.get((long) ((i - 1) % 3 + 1)));
            machine.setStatus(MachineStatus.AVAILABLE);
            testMachines.add(machine);
        }

        List<Schedule> testSchedules = createComplexTestSchedules();

        when(scheduleRepository.findAll()).thenReturn(testSchedules);
        when(machineRepository.findAll()).thenReturn(testMachines);

        scheduler = new Scheduler(scheduleRepository, machineRepository,
                new ModelMapperExtended(), objectMapper);
    }

    private List<Schedule> createComplexTestSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now();

        for (int i = 1; i <= 9; i++) {
            Job job = new Job();
            job.setId((long) i);

            if (i <= 3) job.setPriority(JobPriority.HIGH);
            else if (i <= 6) job.setPriority(JobPriority.MEDIUM);
            else job.setPriority(JobPriority.LOW);

            Schedule schedule = new Schedule();
            schedule.setId((long) i);
            schedule.setJob(job);
            schedule.setMachineType(machineTypes.get((long) ((i - 1) % 3 + 1)));
            schedule.setStartTime(baseTime.plusHours(i));

            if (i % 3 == 0) schedule.setDuration(7200L);
            else if (i % 3 == 1) schedule.setDuration(3600L);
            else schedule.setDuration(1800L);

            schedule.setDueDate(baseTime.plusDays(i % 3 + 1));
            schedule.setStatus(ScheduleStatus.PENDING);

            schedules.add(schedule);
        }

        return schedules;
    }

    @Test
    void testScheduleByPriority() {
        scheduler.scheduleByPriority();

        List<Schedule> scheduledJobs = scheduleRepository.findAll();

        List<Schedule> highPriorityJobs = scheduledJobs.stream()
                .filter(s -> s.getJob().getPriority() == JobPriority.HIGH)
                .toList();

        assertFalse(highPriorityJobs.isEmpty(), "Should have high priority jobs");

        verifyMachineAssignments(scheduledJobs);

        verifyNoTimeConflicts(scheduledJobs);

        verifyPriorityOrdering(scheduledJobs);
    }

    @Test
    void testScheduleByDueDate() {
        scheduler.scheduleByDueDate();

        List<Schedule> scheduledJobs = scheduleRepository.findAll();

        verifyDueDatesRespected(scheduledJobs);

        verifyMachineTypeCompatibility(scheduledJobs);

        verifyLoadBalancing(scheduledJobs);
    }

    @Test
    void testScheduleByDuration() {
        scheduler.scheduleByDuration();

        List<Schedule> scheduledJobs = scheduleRepository.findAll();

        verifyDurationPreference(scheduledJobs);

        verifyMachineUtilization(scheduledJobs);
    }

    @Test
    void testScheduleByEveryType() {
        scheduler.scheduleByEveryType();

        verify(scheduleRepository, times(3)).findAll();
        verify(machineRepository, times(3)).findAll();

        List<Schedule> finalSchedule = scheduleRepository.findAll();
        verifyValidSchedule(finalSchedule);
    }

    @Test
    void testScheduleWithNoJobs() {
        when(scheduleRepository.findAll()).thenReturn(Collections.emptyList());

        // Non dovrebbe lanciare eccezioni e semplicemente non schedulare nulla.
        scheduler.scheduleByPriority();

        // Nessun job è stato schedulato, poiché non esiste alcun job
        List<Schedule> schedules = scheduleRepository.findAll();
        assertTrue(schedules.isEmpty(), "There should be no jobs to schedule");
    }

    /*
    @Test
    void testJobsWithPastDueDates() {
        List<Schedule> testSchedules = createComplexTestSchedules();
        // Forzo la dueDate di tutti i job a una data passata
        for (Schedule s : testSchedules) {
            s.setDueDate(LocalDateTime.now().minusDays(1));
        }
        when(scheduleRepository.findAll()).thenReturn(testSchedules);

        scheduler.scheduleByDueDate();

        List<Schedule> result = scheduleRepository.findAll();
        // I job non devono essere schedulati perché la dueDate è già passata
        boolean allPending = result.stream().allMatch(s -> s.getStatus() == ScheduleStatus.PENDING);
        assertTrue(allPending, "Jobs with dueDate already passed should not be scheduled");
    }
     */

    /*
    @Test
    void testUnfeasibleJobConstraint() {
        LocalDateTime now = LocalDateTime.now();

        Job infeasibleJob = new Job();
        infeasibleJob.setId(100L);
        infeasibleJob.setPriority(JobPriority.HIGH);

        Schedule infeasibleSchedule = new Schedule();
        infeasibleSchedule.setId(100L);
        infeasibleSchedule.setJob(infeasibleJob);
        infeasibleSchedule.setMachineType(machineTypes.get(1L));
        infeasibleSchedule.setStartTime(now);
        // Durata esagerata (ad es. 100 giorni in secondi)
        infeasibleSchedule.setDuration(100L * 24L * 3600L);
        infeasibleSchedule.setDueDate(now.plusDays(1)); // Impossibile da completare entro la dueDate
        infeasibleSchedule.setStatus(ScheduleStatus.PENDING);

        when(scheduleRepository.findAll()).thenReturn(List.of(infeasibleSchedule));

        // Macchine disponibili
        Machine machine = new Machine();
        machine.setId(50L);
        machine.setMachine_type_id(machineTypes.get(1L));
        machine.setStatus(MachineStatus.AVAILABLE);
        when(machineRepository.findAll()).thenReturn(List.of(machine));

        scheduler.scheduleByDueDate();

        // Controlliamo se la schedule è rimasta PENDING o se lo scheduler ha gestito il caso
        List<Schedule> schedules = scheduleRepository.findAll();
        Schedule result = schedules.get(0);
        assertEquals(ScheduleStatus.PENDING, result.getStatus(),
                "The schedule should not be modified since it is not schedulable");
    }
     */

    /*
    @Test
    void testSingleMachineMultipleJobs() {
        // Creo alcune schedule con durate e priorità diverse
        List<Schedule> testSchedules = createComplexTestSchedules();

        when(scheduleRepository.findAll()).thenReturn(testSchedules);

        // Una sola macchina disponibile
        Machine singleMachine = new Machine();
        singleMachine.setId(10L);
        singleMachine.setMachine_type_id(machineTypes.get(1L));
        singleMachine.setStatus(MachineStatus.AVAILABLE);

        when(machineRepository.findAll()).thenReturn(List.of(singleMachine));

        scheduler.scheduleByPriority();
        List<Schedule> scheduledJobs = scheduleRepository.findAll();

        // Verifico che tutte le schedule siano assegnate alla stessa macchina
        Set<Machine> usedMachines = scheduledJobs.stream().map(Schedule::getMachine).collect(Collectors.toSet());
        assertEquals(1, usedMachines.size(), "All schedules must be assigned to the same machine");

        // Verifico che non vi siano conflitti temporali
        verifyNoTimeConflicts(scheduledJobs);

        // Verifico che le job ad alta priorità siano effettivamente più vicine all'inizio
        verifyPriorityOrdering(scheduledJobs);
    }
     */

    @Test
    void testScheduleWithNoAvailableMachines() {
        List<Schedule> testSchedules = createComplexTestSchedules();
        when(scheduleRepository.findAll()).thenReturn(testSchedules);
        when(machineRepository.findAll()).thenReturn(new ArrayList<>()); // Nessuna macchina

        // Tentativo di schedulazione
        scheduler.scheduleByDueDate();

        // Qui ci aspettiamo che nessun job venga schedulato poiché non ci sono macchine
        List<Schedule> scheduledJobs = scheduleRepository.findAll();
        // Non necessariamente vuoto, ma ci aspettiamo che lo scheduler non modifichi lo stato a "SCHEDULED"
        // Possiamo controllare che siano tutti ancora in PENDING
        boolean allPending = scheduledJobs.stream().allMatch(s -> s.getStatus() == ScheduleStatus.PENDING);
        assertTrue(allPending, "Without available machines, all jobs should remain in their original state");
    }

    private void verifyMachineAssignments(List<Schedule> schedules) {
        schedules.forEach(schedule -> {
            assertNotNull(schedule.getMachine(), "Each schedule should have a machine assigned");
            assertEquals(schedule.getMachineType(),
                    schedule.getMachine().getMachine_type_id(),
                    "Machine type should match schedule requirements");
        });
    }

    private void verifyNoTimeConflicts(List<Schedule> schedules) {
        for (Schedule s1 : schedules) {
            for (Schedule s2 : schedules) {
                if (s1 != s2 && Objects.equals(s1.getMachine(), s2.getMachine())) {
                    LocalDateTime s1End = s1.getStartTime().plusSeconds(s1.getDuration());
                    LocalDateTime s2End = s2.getStartTime().plusSeconds(s2.getDuration());

                    assertFalse(
                            (s1.getStartTime().isBefore(s2End) && s1End.isAfter(s2.getStartTime())),
                            String.format("Time conflict found between schedules %d and %d",
                                    s1.getId(), s2.getId())
                    );
                }
            }
        }
    }

    private void verifyPriorityOrdering(List<Schedule> schedules) {
        List<Schedule> sortedSchedules = schedules.stream()
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        int highPriorityEarly = 0;
        int totalHighPriority = 0;

        for (int i = 0; i < sortedSchedules.size() / 2; i++) {
            if (sortedSchedules.get(i).getJob().getPriority() == JobPriority.HIGH) {
                highPriorityEarly++;
            }
        }

        for (Schedule schedule : sortedSchedules) {
            if (schedule.getJob().getPriority() == JobPriority.HIGH) {
                totalHighPriority++;
            }
        }

        assertTrue(highPriorityEarly > totalHighPriority / 3,
                "Most high priority jobs should be scheduled earlier");
    }

    private void verifyMachineTypeCompatibility(List<Schedule> schedules) {
        schedules.forEach(schedule -> {
            assertNotNull(schedule.getMachine(), "Machine must be assigned");
            assertEquals(schedule.getMachineType(),
                    schedule.getMachine().getMachine_type_id(),
                    "Machine type must match schedule requirements");
        });
    }

    private void verifyLoadBalancing(List<Schedule> schedules) {
        Map<Machine, Long> machineLoadMap = schedules.stream()
                .collect(Collectors.groupingBy(
                        Schedule::getMachine,
                        Collectors.summingLong(Schedule::getDuration)
                ));

        double averageLoad = machineLoadMap.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        double allowedDifferencePercentage = 2.0;

        boolean isLoadBalanced = machineLoadMap.values().stream()
                .allMatch(load -> Math.abs(load - averageLoad) <= averageLoad * allowedDifferencePercentage);

        assertTrue(isLoadBalanced, "Load should be reasonably balanced across machines");
    }

    private void verifyDurationPreference(List<Schedule> schedules) {
        List<Schedule> sortedSchedules = schedules.stream()
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        long earlierJobsAvgDuration = (long) sortedSchedules.subList(0, sortedSchedules.size() / 2)
                .stream()
                .mapToLong(Schedule::getDuration)
                .average()
                .orElse(0);

        long laterJobsAvgDuration = (long) sortedSchedules.subList(sortedSchedules.size() / 2, sortedSchedules.size())
                .stream()
                .mapToLong(Schedule::getDuration)
                .average()
                .orElse(0);

        assertTrue(earlierJobsAvgDuration <= laterJobsAvgDuration,
                "Shorter jobs should tend to be scheduled earlier");
    }

    private void verifyMachineUtilization(List<Schedule> schedules) {
        Map<Machine, Long> machineUtilization = schedules.stream()
                .collect(Collectors.groupingBy(
                        Schedule::getMachine,
                        Collectors.summingLong(Schedule::getDuration)
                ));

        assertFalse(machineUtilization.isEmpty(), "Some machines should be utilized");
        assertTrue(machineUtilization.size() > 1, "Multiple machines should be utilized");
    }

    private void verifyValidSchedule(List<Schedule> schedules) {
        assertFalse(schedules.isEmpty(), "Schedule should not be empty");
        verifyNoTimeConflicts(schedules);
        verifyMachineTypeCompatibility(schedules);
        verifyDueDatesRespected(schedules);
    }

    private void verifyDueDatesRespected(List<Schedule> schedules) {
        schedules.forEach(schedule -> {
            LocalDateTime endTime = schedule.getStartTime().plusSeconds(schedule.getDuration());
            assertTrue(
                    endTime.isBefore(schedule.getDueDate()) ||
                            endTime.isEqual(schedule.getDueDate()),
                    String.format("Schedule %d exceeds its due date", schedule.getId())
            );
        });
    }
}