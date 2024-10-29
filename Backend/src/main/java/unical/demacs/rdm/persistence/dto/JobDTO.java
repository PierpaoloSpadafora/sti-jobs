package unical.demacs.rdm.persistence.dto;

import lombok.Data;
import unical.demacs.rdm.persistence.enums.JobPriority;
import unical.demacs.rdm.persistence.enums.JobStatus;

@Data
public class JobDTO {

    private Long id;
    private String title;
    private String description;
    private JobStatus status;
    private AssigneeDTO assignee;
    private JobPriority priority;
    private String duration;  // Duration come stringa ISO-8601 (es. "PT2H30M")
    private MachineTypeDTO requiredMachineType;

    @Data
    public static class AssigneeDTO {
        private String id;  // ID dell'utente (String per UUID)
        private String email;
    }

    @Data
    public static class MachineTypeDTO {
        private Long id;
        private String name;
        private String description;
    }
}
