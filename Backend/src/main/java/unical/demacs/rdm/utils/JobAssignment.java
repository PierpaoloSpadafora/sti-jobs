package unical.demacs.rdm.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import unical.demacs.rdm.persistence.entities.Machine;
import unical.demacs.rdm.persistence.entities.Schedule;

import java.util.concurrent.atomic.AtomicLong;

@Data
@NoArgsConstructor
@PlanningEntity
public class JobAssignment {
    private static final AtomicLong idCounter = new AtomicLong();

    @PlanningId
    private Long id;
    private Schedule schedule;

    @PlanningVariable(valueRangeProviderRefs = "machineRange")
    private Machine assignedMachine;

    @PlanningVariable(valueRangeProviderRefs = "timeGrainRange")
    private TimeGrain startTimeGrain;

    public JobAssignment(Schedule schedule) {
        this.schedule = schedule;
        this.id = idCounter.incrementAndGet();
    }
    public Long getStartTimeInSeconds() {
        return startTimeGrain != null ? startTimeGrain.getStartTimeInSeconds() : null;
    }

    public Long getEndTimeInSeconds() {
        return getStartTimeInSeconds() != null ? getStartTimeInSeconds() + schedule.getDuration() : null;
    }

    @Override
    public String toString() {
        return String.format("JobAssignment{id=%d, scheduleId=%d, machineId=%s, startTime=%s, endTime=%s}",
                id,
                schedule != null ? schedule.getId() : null,
                assignedMachine != null ? assignedMachine.getId() : "unassigned",
                startTimeGrain != null ? startTimeGrain.getStartTimeInSeconds() : "unscheduled",
                getEndTimeInSeconds() != null ? getEndTimeInSeconds() : "unknown");
    }
}

