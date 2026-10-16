package com.andres_lozada.demo.service;

import com.andres_lozada.demo.dto.CreateCustomerRequest;
import com.andres_lozada.demo.dto.CustomerResponse;
import com.andres_lozada.demo.entity.CustomerEntity;
import com.andres_lozada.demo.exception.CustomerNotFoundException;
import com.andres_lozada.demo.exception.ValidationException;
import com.andres_lozada.demo.mapper.CustomerMapper;
import com.andres_lozada.demo.repository.CustomerRepository;
import com.andres_lozada.demo.security.InputSanitizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock CustomerRepository customerRepository;
    @Mock CustomerMapper customerMapper;
    @Mock AuditService auditService;
    @Mock InputSanitizer inputSanitizer;
    @InjectMocks CustomerService customerService;

    @Test
    void rejectsDuplicateEmail() {
        var request = CreateCustomerRequest.builder().email("a@example.com").firstName("A").lastName("B").build();
        when(inputSanitizer.sanitizeString("a@example.com")).thenReturn("a@example.com");
        when(inputSanitizer.sanitizeString("A")).thenReturn("A");
        when(inputSanitizer.sanitizeString("B")).thenReturn("B");
        when(inputSanitizer.sanitizeString(null)).thenReturn(null);
        when(customerRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new com.andres_lozada.demo.entity.CustomerEntity()));
        assertThrows(ValidationException.class, () -> customerService.createCustomer(request));
    }

    @Test
    void createsCustomerWithNormalizedFields() {
        var request = CreateCustomerRequest.builder().email(" a@example.com ").firstName(" Ana ").lastName(" Gomez ").build();
        CustomerEntity entity = new CustomerEntity();
        when(inputSanitizer.sanitizeString(" a@example.com ")).thenReturn("a@example.com");
        when(inputSanitizer.sanitizeString(" Ana ")).thenReturn("Ana");
        when(inputSanitizer.sanitizeString(" Gomez ")).thenReturn("Gomez");
        when(inputSanitizer.sanitizeString(null)).thenReturn(null);
        when(customerRepository.findByEmail("a@example.com")).thenReturn(Optional.empty());
        when(customerMapper.toEntity(request)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(entity);
        when(customerMapper.toResponse(entity)).thenReturn(CustomerResponse.builder().email("a@example.com").build());

        assertEquals("a@example.com", customerService.createCustomer(request).getEmail());
        assertEquals("a@example.com", request.getEmail());
    }

    @Test
    void getCustomerThrowsWhenMissing() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomer(customerId));
    }
}
