package org.emsi.EBank_Service.web;

import java.util.List;

import org.emsi.EBank_Service.dto.BankAccountRequestDTO;
import org.emsi.EBank_Service.dto.BankAccountResponseDTO;
import org.emsi.EBank_Service.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bankAccounts")
public class BankAccountRestController {

    private final BankAccountService bankAccountService;

    public BankAccountRestController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public List<BankAccountResponseDTO> findAll() {
        return bankAccountService.findAll();
    }

    @GetMapping("/{id}")
    public BankAccountResponseDTO findById(@PathVariable String id) {
        return bankAccountService.findById(id);
    }

    // Tous les comptes d'un client, ex : GET /api/bankAccounts/customer/1
    @GetMapping("/customer/{customerId}")
    public List<BankAccountResponseDTO> findByCustomer(@PathVariable Long customerId) {
        return bankAccountService.findByCustomerId(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponseDTO create(@Valid @RequestBody BankAccountRequestDTO request) {
        return bankAccountService.create(request);
    }

    @PutMapping("/{id}")
    public BankAccountResponseDTO update(@PathVariable String id, @Valid @RequestBody BankAccountRequestDTO request) {
        return bankAccountService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        bankAccountService.delete(id);
    }
}
