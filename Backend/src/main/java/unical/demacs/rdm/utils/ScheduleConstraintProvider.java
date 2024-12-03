package unical.demacs.rdm.utils;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class ScheduleConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                machineConflict(constraintFactory),
                respectDueDates(constraintFactory),
                prioritizeHighPriorityJobs(constraintFactory)
        };
    }

    private Constraint machineConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(JobAssignment.class,
                        Joiners.equal(JobAssignment::getAssignedMachine),
                        Joiners.overlapping(JobAssignment::getStartTime, JobAssignment::getEndTime))
                .penalize("Machine conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint respectDueDates(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .filter(ja -> ja.getEndTime().isAfter(ja.getJob().getDueDate()))
                .penalize("Job due date", HardSoftScore.ONE_HARD);
    }

    private Constraint prioritizeHighPriorityJobs(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(JobAssignment.class)
                .reward("High priority jobs first", HardSoftScore.ONE_SOFT, ja -> ja.getJob().getPriority().ordinal());
    }
}
