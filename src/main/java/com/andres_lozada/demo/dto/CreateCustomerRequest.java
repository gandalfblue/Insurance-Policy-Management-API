package com.andres_lozada.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO for creating a new customer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
}
