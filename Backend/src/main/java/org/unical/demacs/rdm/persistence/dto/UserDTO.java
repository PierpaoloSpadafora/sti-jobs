package org.unical.demacs.rdm.persistence.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class UserDTO {
    //@NotBlank(message = "The ID cannot be empty")
    private String id;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Provide a valid email address")
    private String email;

}
