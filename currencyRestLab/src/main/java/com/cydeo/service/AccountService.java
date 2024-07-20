package com.cydeo.service;

import com.cydeo.dto.AccountDTO;

import java.util.List;

public interface AccountService {

    List<AccountDTO> findAllByUsername(String username);

    AccountDTO create (AccountDTO accountDTO);
     List<AccountDTO> findByUserNameAndCurrency(String username, List<String> currency);


}
