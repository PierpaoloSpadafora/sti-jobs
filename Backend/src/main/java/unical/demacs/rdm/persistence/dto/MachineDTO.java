package unical.demacs.rdm.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unical.demacs.rdm.persistence.enums.MachineStatus;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineDTO {
    private Long id;
    private String name;
    private String description;
    private MachineStatus status;
    private Long typeId;
    private String typeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}