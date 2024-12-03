package unical.demacs.rdm.utils;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.time.ZoneOffset;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;

public class ScheduleConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                machineConflict(constraintFactory),       // Impedire la sovrapposizione di lavori sulla stessa macchina
                respectDueDates(constraintFactory),       // I lavori devono essere completati prima della scadenza
                jobsStartAfterStartDate(constraintFactory), // I lavori non possono iniziare prima della data di inizio.

                balanceMachineLoad(constraintFactory),    // Distribuire il lavoro in modo uniforme
                prioritizeHighPriorityJobs(constraintFactory), // Pianificare prima i lavori ad alta priorità
                prioritizeShortDurationJobs(constraintFactory), // Preferire lavori più brevi
                encourageParallelExecution(constraintFactory),  // Massimizzare l'utilizzo della macchina
                //oneAssignmentPerSchedule(constraintFactory)
        };
    }

    private Constraint machineConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(JobAssignment.class,
                        Joiners.equal(JobAssignment::getAssignedMachine))
                .filter((ja1, ja2) -> {
                    if (ja1.getStartTimeInSeconds() == null || ja2.getStartTimeInSeconds() == null) {
                        return false;
                    }
                    // qui penalizzo la scelta di usare la stessa macchina nello stesso lasso di tempo
                    // MA NON FA NIENTE, continua a scegliere la stessa macchina e a spostare i lavori
                    return ja1.getStartTimeInSeconds() <= ja2.getStartTimeInSeconds() &&
                            ja1.getEndTimeInSeconds() > ja2.getStartTimeInSeconds() &&
                            ja1.getAssignedMachine() == ja2.getAssignedMachine();
                })
                .penalize(HardSoftScore.ONE_HARD.multiply(1000))
                .asConstraint("Machine conflict");
    }

    private Constraint respectDueDates(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .filter(ja -> {
                    if (ja.getStartTimeInSeconds() == null || ja.getSchedule().getDueDate() == null)
                        return false;
                    return ja.getEndTimeInSeconds() >
                            ja.getSchedule().getDueDate().toEpochSecond(ZoneOffset.UTC);
                })
                .penalize(HardSoftScore.ONE_HARD.multiply(100))
                .asConstraint("Job due date");
    }

    private Constraint jobsStartAfterStartDate(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .filter(ja -> {
                    if (ja.getStartTimeGrain() == null) return false;
                    return ja.getStartTimeInSeconds() <
                            ja.getSchedule().getStartTime().toEpochSecond(ZoneOffset.UTC);
                })
                .penalize(HardSoftScore.ONE_HARD.multiply(1000))
                .asConstraint("Jobs must start after start date");
    }

    private Constraint balanceMachineLoad(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .groupBy(JobAssignment::getAssignedMachine,
                        sum(ja -> Math.toIntExact(ja.getSchedule().getDuration())))
                .penalize(HardSoftScore.ONE_SOFT.multiply(5),
                        (machine, totalDuration) -> (totalDuration * totalDuration / 3600))
                .asConstraint("Balance machine load");
    }

    private Constraint prioritizeHighPriorityJobs(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .reward(HardSoftScore.ONE_SOFT.multiply(3),
                        ja -> ja.getSchedule().getJob().getPriority().ordinal() * 100)
                .asConstraint("High priority jobs first");
    }

    private Constraint prioritizeShortDurationJobs(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .reward(HardSoftScore.ONE_SOFT.multiply(2),
                        ja -> (int)(7200 - Math.min(ja.getSchedule().getDuration(), 7200)))
                .asConstraint("Short duration jobs first");
    }

    private Constraint encourageParallelExecution(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(JobAssignment.class)
                .filter((ja1, ja2) ->
                        ja1.getSchedule().getMachineType().getId().equals(
                                ja2.getSchedule().getMachineType().getId()))
                .filter((ja1, ja2) -> ja1.getAssignedMachine() == ja2.getAssignedMachine())
                .filter((ja1, ja2) -> {
                    if (ja1.getStartTimeInSeconds() == null || ja2.getStartTimeInSeconds() == null) {
                        return false;
                    }
                    return Math.abs(ja1.getStartTimeInSeconds() - ja2.getStartTimeInSeconds()) < 3600;
                })
                .penalize(HardSoftScore.ONE_SOFT.multiply(10),
                        (ja1, ja2) -> 1000)
                .asConstraint("Encourage parallel execution");
    }

    private Constraint oneAssignmentPerSchedule(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .groupBy(JobAssignment::getSchedule, ConstraintCollectors.count())
                .filter((schedule, count) -> count > 1)
                .penalize(HardSoftScore.ONE_HARD,
                        (schedule, count) -> count - 1)
                .asConstraint("One assignment per schedule");
    }

}