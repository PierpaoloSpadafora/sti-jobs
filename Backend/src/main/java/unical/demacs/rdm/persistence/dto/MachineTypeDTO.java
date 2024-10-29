package unical.demacs.rdm.persistence.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineTypeDTO {
    private Long id;
    private String name;
    private String description;
}
