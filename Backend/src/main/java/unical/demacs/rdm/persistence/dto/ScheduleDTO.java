package unical.demacs.rdm.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {

    private Long id;
    private Long jobId;
    private String machineType;
    private LocalDateTime dueDate;
    private LocalDateTime startTime;
    private Long duration;
    private String status;

}