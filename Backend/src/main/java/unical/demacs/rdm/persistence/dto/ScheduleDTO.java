package unical.demacs.rdm.persistence.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ScheduleDTO {
    private Long scheduleId;
    private Long jobId;
    private Long machineId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
