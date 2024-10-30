package unical.demacs.rdm.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {
    private Long id;
    private String title;
    private String description;
    private JobStatus status;
    private UserDTO assignee;
    private JobPriority priority;

    private long duration; // Durata in secondi o millisecondi

    private MachineTypeDTO requiredMachineType;
}