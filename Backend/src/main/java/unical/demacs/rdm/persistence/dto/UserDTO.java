package unical.demacs.rdm.persistence.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private String id;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Provide a valid email address")
    private String email;

}
