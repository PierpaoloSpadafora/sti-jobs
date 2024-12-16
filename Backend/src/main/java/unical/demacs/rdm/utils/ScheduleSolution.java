package unical.demacs.rdm.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.domain.solution.*;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import unical.demacs.rdm.persistence.entities.Machine;

import java.util.List;

@Slf4j
@PlanningSolution
@NoArgsConstructor
@Data
public class ScheduleSolution {

    @ProblemFactProperty
    private ScheduleConstraintConfiguration constraintConfiguration;

    @PlanningEntityCollectionProperty
    private List<JobAssignment> jobAssignments;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "machineRange")
    private List<Machine> machines;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeGrainRange")
    private List<TimeGrain> timeGrainRange;

    @PlanningScore
    private HardSoftScore score;

    public ScheduleSolution(List<JobAssignment> jobAssignments, List<Machine> machines, List<TimeGrain> timeGrainRange, ScheduleConstraintConfiguration constraintConfiguration) {
        this.jobAssignments = jobAssignments;
        this.machines = machines;
        this.timeGrainRange = timeGrainRange;
        this.constraintConfiguration = constraintConfiguration;
        log.debug("Creata ScheduleSolution con {} jobAssignments, {} machines, {} timeGrains",
                jobAssignments.size(), machines.size(), timeGrainRange.size());
    }
}
