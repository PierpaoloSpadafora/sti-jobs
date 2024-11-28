package unical.demacs.rdm.persistence.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Il titolo è obbligatorio")
    @Size(min = 1, max = 255, message = "Il titolo deve essere tra 1 e 255 caratteri")
    private String title;

    @Size(max = 1000, message = "La descrizione può contenere al massimo 1000 caratteri")
    private String description;

    @NotNull(message = "Lo stato è obbligatorio")
    private JobStatus status;

    @NotNull(message = "La priorità è obbligatoria")
    private JobPriority priority;

    @PositiveOrZero(message = "La durata deve essere un valore positivo o zero")
    private long duration;

    @NotNull(message = "Il tipo di macchina richiesto è obbligatorio")
    private long idMachineType;

    private String assigneeEmail;
}
