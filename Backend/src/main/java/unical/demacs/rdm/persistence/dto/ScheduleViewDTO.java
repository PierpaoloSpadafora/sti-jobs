package unical.demacs.rdm.persistence.dto;

import lombok.Data;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;

import java.time.LocalDateTime;

@Data
public class ScheduleViewDTO {
    private Long id;
    private Long jobId;
    private String jobName;
    private String machineType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private JobPriority priority;
}