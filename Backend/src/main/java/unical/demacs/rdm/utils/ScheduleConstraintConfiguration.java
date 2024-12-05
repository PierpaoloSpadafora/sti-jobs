package unical.demacs.rdm.utils;

import lombok.Data;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@Data
@ConstraintConfiguration
public class ScheduleConstraintConfiguration {

    @ConstraintWeight("Machine conflict")
    private HardSoftScore machineConflict = HardSoftScore.ofHard(1);

    @ConstraintWeight("Machine type compatibility")
    private HardSoftScore machineTypeCompatibility = HardSoftScore.ofHard(1000);

    @ConstraintWeight("Job due date")
    private HardSoftScore jobDueDate = HardSoftScore.ofHard(1);

    @ConstraintWeight("High priority jobs first")
    private HardSoftScore highPriorityJobsFirst = HardSoftScore.ofSoft(1);

    @ConstraintWeight("Short duration jobs first")
    private HardSoftScore shortDurationJobsFirst = HardSoftScore.ofSoft(1);

    @ConstraintWeight("Balance machine load")
    private HardSoftScore balanceMachineLoad = HardSoftScore.ofSoft(1000);

    @Override
    public String toString() {
        return "ScheduleConstraintConfiguration{" +
                "machineConflict=" + machineConflict +
                ", machineTypeCompatibility=" + machineTypeCompatibility +
                ", jobDueDate=" + jobDueDate +
                ", highPriorityJobsFirst=" + highPriorityJobsFirst +
                ", shortDurationJobsFirst=" + shortDurationJobsFirst +
                ", balanceMachineLoad=" + balanceMachineLoad +
                '}';
    }
}
