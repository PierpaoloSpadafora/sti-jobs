package unical.demacs.rdm.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unical.demacs.rdm.persistence.enums.ScheduleStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {

    private Long id;
    private Long jobId;
    private long machineTypeId;
    private LocalDateTime dueDate;
    private LocalDateTime startTime;
    private Long duration;
    private ScheduleStatus status;

}