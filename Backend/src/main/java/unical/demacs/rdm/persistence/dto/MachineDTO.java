package unical.demacs.rdm.persistence.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Il nome è obbligatorio")
    @Size(min = 1, max = 255, message = "Il nome deve essere tra 1 e 255 caratteri")
    private String name;

    @Size(max = 1000, message = "La descrizione può contenere al massimo 1000 caratteri")
    private String description;

    @NotNull(message = "Lo stato è obbligatorio")
    private MachineStatus status;

    @NotNull(message = "Il tipo di macchina è obbligatorio")
    private Long typeId;

    private String typeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
