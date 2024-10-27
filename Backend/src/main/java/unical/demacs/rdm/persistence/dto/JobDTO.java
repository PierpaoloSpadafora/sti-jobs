package unical.demacs.rdm.persistence.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class JobDTO {
    private Long jobId;
    private String title;
    private String description;
    private String status;
    private Long assigneeId;
    private String priority;
    private LocalDateTime dueDate;
    private Long machineId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}