package com.andres_lozada.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO for updating an existing customer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {

    @Email
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
}
