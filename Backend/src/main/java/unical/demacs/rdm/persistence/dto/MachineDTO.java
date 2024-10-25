package unical.demacs.rdm.persistence.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MachineDTO {
    private Long machineId;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}