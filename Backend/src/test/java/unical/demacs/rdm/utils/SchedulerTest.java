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

        verify(scheduleRepository, times(5)).findAll();
        verify(machineRepository, times(5)).findAll();

        List<Schedule> finalSchedule = scheduleRepository.findAll();
        verifyValidSchedule(finalSchedule);
    }

    @Test
    void testScheduleWithNoJobs() {
        when(scheduleRepository.findAll()).thenReturn(Collections.emptyList());

        scheduler.scheduleByPriority();

        List<Schedule> schedules = scheduleRepository.findAll();
        assertTrue(schedules.isEmpty(), "There should be no jobs to schedule");
    }


    @Test
    void testScheduleWithNoAvailableMachines() {
        List<Schedule> testSchedules = createComplexTestSchedules();
        when(scheduleRepository.findAll()).thenReturn(testSchedules);
        when(machineRepository.findAll()).thenReturn(new ArrayList<>());

        scheduler.scheduleByDueDate();

        List<Schedule> scheduledJobs = scheduleRepository.findAll();
        boolean allPending = scheduledJobs.stream().allMatch(s -> s.getStatus() == ScheduleStatus.PENDING);
        assertTrue(allPending, "Without available machines, all jobs should remain in their original state");
    }

    @Test
    void testScheduleByFCFS() {
        // Esegui lo scheduling FCFS
        scheduler.scheduleByFCFS();

        // Recupera tutte le schedule post-schedulazione
        List<Schedule> scheduledJobs = scheduleRepository.findAll();

        // Verifica che le schedule abbiano macchine assegnate
        verifyMachineAssignments(scheduledJobs);

        // Verifica l’assenza di conflitti temporali
        verifyNoTimeConflicts(scheduledJobs);

        // Verifica compatibilità del tipo macchina
        verifyMachineTypeCompatibility(scheduledJobs);

        // Verifica l’ordinamento FCFS:
        // Semplicemente controlliamo che i job, una volta schedulati,
        // rimangano in ordine non decrescente di startTime.
        List<Schedule> sortedByStartTime = scheduledJobs.stream()
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();
        assertEquals(sortedByStartTime, scheduledJobs,
                "Schedules should be sorted by start time (FCFS order)");

        // Nessuna dueDate superata
        verifyDueDatesRespected(scheduledJobs);
    }

    @Test
    void testScheduleByRR() {
        // Esegui lo scheduling RR
        scheduler.scheduleByRR();

        // Recupera tutte le schedule post-schedulazione
        List<Schedule> scheduledJobs = scheduleRepository.findAll();

        // Verifica che le schedule abbiano macchine assegnate
        verifyMachineAssignments(scheduledJobs);

        // Verifica l’assenza di conflitti temporali
        verifyNoTimeConflicts(scheduledJobs);

        // Verifica compatibilità del tipo macchina
        verifyMachineTypeCompatibility(scheduledJobs);

        // Verifica se le schedule sono effettivamente "distribuite" tra le macchine
        // in modo bilanciato (o almeno non tutte sulla stessa macchina).
        Map<Machine, Long> machineCount = scheduledJobs.stream()
                .collect(Collectors.groupingBy(Schedule::getMachine, Collectors.counting()));
        // Nel Round Robin puro, nessuna macchina dovrebbe avere troppi job rispetto alle altre.
        // Ad esempio, se ci sono 6 macchine e 9 job, ogni macchina dovrebbe avere 1 o 2 job.
        long maxCount = Collections.max(machineCount.values());
        long minCount = Collections.min(machineCount.values());
        assertTrue((maxCount - minCount) <= 1,
                "Jobs should be fairly distributed among machines in round-robin manner");

        // Nessuna dueDate superata
        verifyDueDatesRespected(scheduledJobs);
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