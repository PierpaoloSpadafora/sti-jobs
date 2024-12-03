package unical.demacs.rdm.utils;

import lombok.Data;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import unical.demacs.rdm.persistence.entities.Machine;

import java.util.List;

@Data
@PlanningSolution
public class ScheduleSolution {
    @PlanningEntityCollectionProperty
    private List<JobAssignment> jobAssignments;

    @ProblemFactCollectionProperty
    private List<Machine> machines;

    @ValueRangeProvider(id = "machineRange")
    public List<Machine> getMachines() {
        return machines;
    }

    private HardSoftScore score;

}


