package org.emsi.Customer_Service.service;

import java.util.List;

import org.emsi.Customer_Service.dto.CustomerRequestDTO;
import org.emsi.Customer_Service.dto.CustomerResponseDTO;
import org.emsi.Customer_Service.entities.Customer;
import org.emsi.Customer_Service.exceptions.CustomerNotFoundException;
import org.emsi.Customer_Service.exceptions.EmailAlreadyUsedException;
import org.emsi.Customer_Service.mappers.CustomerMapper;
import org.emsi.Customer_Service.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll().stream().map(customerMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO findById(Long id) {
        return customerMapper.toResponse(getCustomer(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> searchByName(String keyword) {
        return customerRepository.findByNameContainsIgnoreCase(keyword).stream()
                .map(customerMapper::toResponse).toList();
    }

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO request) {
        ensureEmailFree(request.getEmail(), null);
        Customer saved = customerRepository.save(customerMapper.toEntity(request));
        return customerMapper.toResponse(saved);
    }

    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO request) {
        Customer customer = getCustomer(id);
        ensureEmailFree(request.getEmail(), id);
        customerMapper.updateEntity(customer, request);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public void delete(Long id) {
        customerRepository.delete(getCustomer(id));
    }

    private Customer getCustomer(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    // L'email est unique : on renvoie une erreur claire (409) plutôt qu'une erreur SQL
    private void ensureEmailFree(String email, Long currentId) {
        customerRepository.findByEmail(email.trim().toLowerCase())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> { throw new EmailAlreadyUsedException(email); });
    }
}
