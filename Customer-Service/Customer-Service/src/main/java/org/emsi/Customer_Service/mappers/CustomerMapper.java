package org.emsi.Customer_Service.mappers;

import org.emsi.Customer_Service.dto.CustomerRequestDTO;
import org.emsi.Customer_Service.dto.CustomerResponseDTO;
import org.emsi.Customer_Service.entities.Customer;
import org.springframework.stereotype.Component;

/**
 * Convertit Entité <-> DTO.
 */
@Component
public class CustomerMapper {

    public CustomerResponseDTO toResponse(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }

    public Customer toEntity(CustomerRequestDTO request) {
        Customer customer = new Customer();
        updateEntity(customer, request);
        return customer;
    }

    public void updateEntity(Customer customer, CustomerRequestDTO request) {
        customer.setName(request.getName().trim());
        customer.setEmail(request.getEmail().trim().toLowerCase());
    }
}
