package unical.demacs.rdm.persistence.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineTypeDTO {
    private Long id;

    @NotNull(message = "Il nome è obbligatorio")
    @Size(min = 1, max = 255, message = "Il nome deve essere tra 1 e 255 caratteri")
    private String name;

    @Size(max = 1000, message = "La descrizione può contenere al massimo 1000 caratteri")
    private String description;
}
