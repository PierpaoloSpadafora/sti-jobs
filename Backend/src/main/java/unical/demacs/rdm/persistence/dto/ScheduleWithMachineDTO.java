package unical.demacs.rdm.persistence.dto;

import lombok.Data;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;

import java.time.LocalDateTime;

@Data
public class ScheduleWithMachineDTO {
    private Long id;
    private Long jobId;
    private long machineTypeId;
    private Long machineId;
    private LocalDateTime startTime;
    private LocalDateTime dueDate;
    private Long duration;
    private ScheduleStatus status;
}