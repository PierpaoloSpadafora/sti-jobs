package unical.demacs.rdm.utils;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.Machine;

import java.time.LocalDateTime;

@PlanningEntity
public class JobAssignment {
    private Job job;

    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine assignedMachine;

    private LocalDateTime startTime;

}
