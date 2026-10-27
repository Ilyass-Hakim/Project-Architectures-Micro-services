package org.emsi.EBank_Service.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.emsi.EBank_Service.clients.CustomerRestClient;
import org.emsi.EBank_Service.dto.BankAccountRequestDTO;
import org.emsi.EBank_Service.dto.BankAccountResponseDTO;
import org.emsi.EBank_Service.entities.BankAccount;
import org.emsi.EBank_Service.exceptions.BankAccountNotFoundException;
import org.emsi.EBank_Service.exceptions.CustomerNotFoundException;
import org.emsi.EBank_Service.mappers.BankAccountMapper;
import org.emsi.EBank_Service.model.Customer;
import org.emsi.EBank_Service.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final CustomerRestClient customerRestClient;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository,
                                  BankAccountMapper bankAccountMapper,
                                  CustomerRestClient customerRestClient) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.customerRestClient = customerRestClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponseDTO> findAll() {
        // Un seul appel distant pour tous les clients (au lieu d'un appel par compte)
        Map<Long, Customer> customersById = customerRestClient.allCustomers().stream()
                .collect(Collectors.toMap(Customer::getId, Function.identity()));
        return bankAccountRepository.findAll().stream()
                .map(account -> bankAccountMapper.toResponse(account, customersById.get(account.getCustomerId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountResponseDTO findById(String id) {
        BankAccount account = getAccount(id);
        return bankAccountMapper.toResponse(account, fetchCustomer(account.getCustomerId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponseDTO> findByCustomerId(Long customerId) {
        Customer customer = fetchCustomer(customerId);
        if (customer == null) throw new CustomerNotFoundException(customerId);
        return bankAccountRepository.findByCustomerId(customerId).stream()
                .map(account -> bankAccountMapper.toResponse(account, customer))
                .toList();
    }

    @Override
    public BankAccountResponseDTO create(BankAccountRequestDTO request) {
        Customer customer = requireCustomer(request.getCustomerId());
        BankAccount saved = bankAccountRepository.save(bankAccountMapper.toEntity(request));
        return bankAccountMapper.toResponse(saved, customer);
    }

    @Override
    public BankAccountResponseDTO update(String id, BankAccountRequestDTO request) {
        BankAccount account = getAccount(id);
        Customer customer = requireCustomer(request.getCustomerId());
        bankAccountMapper.updateEntity(account, request);
        return bankAccountMapper.toResponse(bankAccountRepository.save(account), customer);
    }

    @Override
    public void delete(String id) {
        bankAccountRepository.delete(getAccount(id));
    }

    private BankAccount getAccount(String id) {
        return bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException(id));
    }

    // Appel OpenFeign : renvoie null si Customer-Service répond 404
    private Customer fetchCustomer(Long customerId) {
        try {
            return customerRestClient.findCustomerById(customerId);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    // On refuse de créer un compte pour un client qui n'existe pas
    private Customer requireCustomer(Long customerId) {
        Customer customer = fetchCustomer(customerId);
        if (customer == null) throw new CustomerNotFoundException(customerId);
        return customer;
    }
}
