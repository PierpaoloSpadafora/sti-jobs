package unical.demacs.rdm.utils;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.optaplanner.core.api.score.stream.Joiners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;

public class ScheduleConstraintProvider implements ConstraintProvider {
    private static final Logger log = LoggerFactory.getLogger(ScheduleConstraintProvider.class);

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        log.debug("Definizione dei vincoli di schedulazione");
        return new Constraint[]{
                // Hard constraints - ridotti i pesi per permettere più flessibilità
                assignmentRequired(constraintFactory),
                machineConflict(constraintFactory),
                machineTypeCompatibility(constraintFactory),
                
                // Soft constraints - aumentati i pesi per incoraggiare l'assegnazione
                encourageAssignment(constraintFactory),
                balanceMachineLoad(constraintFactory),
                prioritizeHighPriorityJobs(constraintFactory),
                prioritizeShortDurationJobs(constraintFactory),
                distributeJobsAcrossMachines(constraintFactory)
        };
    }

    // ---------------------- Hard ----------------------

    private Constraint machineConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(JobAssignment.class,
                        Joiners.equal(JobAssignment::getAssignedMachine))
                .filter((ja1, ja2) -> {
                    if (ja1.getStartTimeInSeconds() == null || ja2.getStartTimeInSeconds() == null) {
                        return false;
                    }
                    long ja1Start = ja1.getStartTimeInSeconds();
                    long ja1End = ja1.getEndTimeInSeconds();
                    long ja2Start = ja2.getStartTimeInSeconds();
                    long ja2End = ja2.getEndTimeInSeconds();
                    return (ja1Start < ja2End && ja2Start < ja1End);
                })
                .penalize(HardSoftScore.ONE_HARD.multiply(1000))
                .asConstraint("Machine conflict");
    }

    private Constraint machineTypeCompatibility(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .filter(ja -> ja.getAssignedMachine() != null &&
                        ja.getSchedule().getMachineType() != null &&
                        !ja.getAssignedMachine().getMachine_type_id().equals(ja.getSchedule().getMachineType()))
                .penalize(HardSoftScore.ONE_HARD.multiply(1000))
                .asConstraint("Machine type compatibility");
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

    private Constraint assignmentRequired(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .filter(ja -> ja.getAssignedMachine() == null || ja.getStartTimeGrain() == null)
                .penalize(HardSoftScore.ONE_HARD.multiply(100000))
                .asConstraint("Assignment required");
    }

    // ---------------------- Soft ----------------------

    private Constraint balanceMachineLoad(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .groupBy(JobAssignment::getAssignedMachine,
                        sum(ja -> Math.toIntExact(ja.getSchedule().getDuration())))
                .penalize(HardSoftScore.ONE_SOFT.multiply(100),
                        (machine, totalDuration) -> totalDuration / 3600)
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

    private Constraint distributeJobsAcrossMachines(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .groupBy(
                    ja -> ja.getSchedule().getMachineType().getId()
                )
                .reward(HardSoftScore.ONE_SOFT.multiply(1000))
                .asConstraint("Distribute jobs across machines");
    }

    private Constraint encourageAssignment(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .filter(ja -> ja.getAssignedMachine() != null && ja.getStartTimeGrain() != null)
                .reward(HardSoftScore.ONE_SOFT.multiply(2000))
                .asConstraint("Encourage assignment");
    }

}