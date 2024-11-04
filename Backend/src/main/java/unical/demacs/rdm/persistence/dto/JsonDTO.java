package unical.demacs.rdm.persistence.dto;

import lombok.Data;
import java.util.List;

@Data
public class JsonDTO {
    private List<JobDTO> jobs;
    private List<MachineDTO> machines;
    private List<MachineTypeDTO> machineTypes;
    private List<ScheduleDTO> schedules;
}

