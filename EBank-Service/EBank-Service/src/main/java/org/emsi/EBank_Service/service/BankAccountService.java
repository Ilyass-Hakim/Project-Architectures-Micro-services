package org.emsi.EBank_Service.service;

import java.util.List;

import org.emsi.EBank_Service.dto.BankAccountRequestDTO;
import org.emsi.EBank_Service.dto.BankAccountResponseDTO;

public interface BankAccountService {
    List<BankAccountResponseDTO> findAll();
    BankAccountResponseDTO findById(String id);
    List<BankAccountResponseDTO> findByCustomerId(Long customerId);
    BankAccountResponseDTO create(BankAccountRequestDTO request);
    BankAccountResponseDTO update(String id, BankAccountRequestDTO request);
    void delete(String id);
}
