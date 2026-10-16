package com.andres_lozada.demo.service;

import com.andres_lozada.demo.audit.Auditable;
import com.andres_lozada.demo.dto.CreateCustomerRequest;
import com.andres_lozada.demo.dto.CustomerResponse;
import com.andres_lozada.demo.dto.UpdateCustomerRequest;
import com.andres_lozada.demo.entity.CustomerEntity;
import com.andres_lozada.demo.enums.AuditOperation;
import com.andres_lozada.demo.exception.CustomerNotFoundException;
import com.andres_lozada.demo.exception.ValidationException;
import com.andres_lozada.demo.mapper.CustomerMapper;
import com.andres_lozada.demo.repository.CustomerRepository;
import com.andres_lozada.demo.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;
    private final InputSanitizer inputSanitizer;

    @Auditable(entity = "CUSTOMER", operation = AuditOperation.CREATE)
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 20)
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        request.setEmail(inputSanitizer.sanitizeString(request.getEmail()));
        request.setFirstName(inputSanitizer.sanitizeString(request.getFirstName()));
        request.setLastName(inputSanitizer.sanitizeString(request.getLastName()));
        request.setPhone(inputSanitizer.sanitizeString(request.getPhone()));
        log.info("Creating customer with email: {}", request.getEmail());
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists: " + request.getEmail(),
                    java.util.List.of(new ValidationException.FieldError("email", "Email must be unique")));
        }
        CustomerEntity entity = customerMapper.toEntity(request);
        try {
            CustomerEntity saved = customerRepository.save(entity);
            return customerMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ValidationException("Email must be unique",
                    java.util.List.of(new ValidationException.FieldError("email", "Email must be unique")), ex);
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CustomerResponse getCustomer(UUID customerId) {
        return customerMapper.toResponse(findOrThrow(customerId));
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CustomerEntity validateCustomerExists(UUID customerId) {
        return findOrThrow(customerId);
    }

    @Auditable(entity = "CUSTOMER", operation = AuditOperation.UPDATE)
    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = 15)
    public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request) {
        request.setEmail(inputSanitizer.sanitizeString(request.getEmail()));
        request.setFirstName(inputSanitizer.sanitizeString(request.getFirstName()));
        request.setLastName(inputSanitizer.sanitizeString(request.getLastName()));
        request.setPhone(inputSanitizer.sanitizeString(request.getPhone()));
        CustomerEntity existing = findOrThrow(customerId);
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(existing.getEmail())) {
            customerRepository.findByEmail(request.getEmail()).ifPresent(other -> {
                throw new ValidationException("Email already exists: " + request.getEmail());
            });
        }
        customerMapper.updateFromRequest(request, existing);
        CustomerEntity saved = customerRepository.save(existing);
        return customerMapper.toResponse(saved);
    }

    @Auditable(entity = "CUSTOMER", operation = AuditOperation.DELETE)
    @Transactional(isolation = Isolation.REPEATABLE_READ, timeout = 15)
    public void deleteCustomer(UUID customerId) {
        customerRepository.delete(findOrThrow(customerId));
    }

    private CustomerEntity findOrThrow(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
    }
}
