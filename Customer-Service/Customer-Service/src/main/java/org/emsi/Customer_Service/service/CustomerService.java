package org.emsi.Customer_Service.service;

import java.util.List;

import org.emsi.Customer_Service.dto.CustomerRequestDTO;
import org.emsi.Customer_Service.dto.CustomerResponseDTO;

/**
 * Couche métier : le controller (web) appelle cette interface,
 * et plus tard les outils MCP de la Partie 2 l'appelleront aussi.
 */
public interface CustomerService {
    List<CustomerResponseDTO> findAll();
    CustomerResponseDTO findById(Long id);
    List<CustomerResponseDTO> searchByName(String keyword);
    CustomerResponseDTO create(CustomerRequestDTO request);
    CustomerResponseDTO update(Long id, CustomerRequestDTO request);
    void delete(Long id);
}
