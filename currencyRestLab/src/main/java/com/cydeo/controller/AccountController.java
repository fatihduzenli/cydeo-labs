package com.cydeo.controller;


import com.cydeo.dto.AccountDTO;
import com.cydeo.dto.ResponseWrapper;
import com.cydeo.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @GetMapping("/all/{username}")
    public ResponseEntity<ResponseWrapper> getAllAccountsByUsername(@PathVariable("username") String username) {

        return ResponseEntity.ok(ResponseWrapper.builder()
                .success(true)
                .message("Accounts are successfully retrieved.")
                .code(HttpStatus.OK.value()).data(accountService.findAllByUsername(username))
                .build());


    }

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper> createAccount(@RequestBody @Valid AccountDTO accountDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.builder().success(true)
                        .message("Account is successfully created.")
                        .code(HttpStatus.CREATED.value())
                        .data(accountService.create(accountDTO))
                        .build());

    }

    @GetMapping("/all/currencies")
    public ResponseEntity<ResponseWrapper> getCertainCurrencies(@RequestParam String username,@RequestParam List<String> currencyList) {

        return ResponseEntity.ok(ResponseWrapper.builder()
                .success(true)
                        .message("Successfully retrieved")
                        .code(HttpStatus.OK.value())
                        .data(accountService.findByUserNameAndCurrency(username,currencyList))
                .build());
    }


}
