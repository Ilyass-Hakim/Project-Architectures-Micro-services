package org.emsi.EBank_Service.service;

import java.util.List;

import org.emsi.EBank_Service.dto.BankAccountRequestDTO;
import org.emsi.EBank_Service.dto.BankAccountResponseDTO;
import org.emsi.EBank_Service.entities.BankAccount;
import org.emsi.EBank_Service.exceptions.BankAccountNotFoundException;
import org.emsi.EBank_Service.mappers.BankAccountMapper;
import org.emsi.EBank_Service.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, BankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponseDTO> findAll() {
        return bankAccountRepository.findAll().stream().map(bankAccountMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountResponseDTO findById(String id) {
        return bankAccountMapper.toResponse(getAccount(id));
    }

    @Override
    public BankAccountResponseDTO create(BankAccountRequestDTO request) {
        BankAccount saved = bankAccountRepository.save(bankAccountMapper.toEntity(request));
        return bankAccountMapper.toResponse(saved);
    }

    @Override
    public BankAccountResponseDTO update(String id, BankAccountRequestDTO request) {
        BankAccount account = getAccount(id);
        bankAccountMapper.updateEntity(account, request);
        return bankAccountMapper.toResponse(bankAccountRepository.save(account));
    }

    @Override
    public void delete(String id) {
        bankAccountRepository.delete(getAccount(id));
    }

    private BankAccount getAccount(String id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException(id));
    }
}
