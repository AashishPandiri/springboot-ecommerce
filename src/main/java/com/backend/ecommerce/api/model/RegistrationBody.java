package com.backend.ecommerce.api.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegistrationBody {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    private String username;

    @Email
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    private String password;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

}
