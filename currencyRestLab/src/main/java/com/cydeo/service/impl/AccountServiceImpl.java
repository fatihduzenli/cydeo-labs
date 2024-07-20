package com.cydeo.service.impl;


import com.cydeo.client.CurrencyClient;
import com.cydeo.dto.AccountDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.dto.response.CurrencyResponse;
import com.cydeo.entity.Account;
import com.cydeo.entity.User;
import com.cydeo.repository.AccountRepository;
import com.cydeo.service.AccountService;
import com.cydeo.service.UserService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final MapperUtil mapperUtil;
    private final CurrencyClient currencyClient;


    public AccountServiceImpl(AccountRepository accountRepository, UserService userService, MapperUtil mapperUtil, CurrencyClient currencyClient) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.currencyClient = currencyClient;

    }

    /**
     * Retrieves all accounts for a user specified by the username.
     *
     * @param username The username of the user.
     * @return List of AccountDTO representing the user's accounts.
     */
    @Override
    public List<AccountDTO> findAllByUsername(String username) {
        // Fetch all accounts for the user by username and convert each to an AccountDTO.
        return accountRepository.findAllByUser_Username(username).stream()
                .map(account -> {
                    // Convert each Account to an AccountDTO and set the username.
                    AccountDTO accountDTO = mapperUtil.convert(account, new AccountDTO());
                    accountDTO.setUsername(username);

                    // here we assigned static values this is a real implementation
//                    Map<String, BigDecimal> otherCurrencies= new LinkedHashMap<>();
//                    otherCurrencies.put("EUR",new BigDecimal(5234));
//                    otherCurrencies.put("CAD",new BigDecimal(75431));

                   accountDTO.setOtherCurrencies(getAllCurrenciesByBalance(accountDTO.getBalance()));
                    return accountDTO;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<AccountDTO> findByUserNameAndCurrency(String username, List<String> currency) {

     return accountRepository.findAllByUser_Username(username).stream()
                .map(account -> {
                    // Convert each Account to an AccountDTO and set the username.
                    AccountDTO accountDTO = mapperUtil.convert(account, new AccountDTO());
                    accountDTO.setUsername(username);
                    accountDTO.setOtherCurrencies(getSpecificCurrenciesByBalance(accountDTO.getBalance(),currency));
                    return accountDTO;
                })
                .collect(Collectors.toList());
    };



    private Map<String, BigDecimal> getSpecificCurrenciesByBalance(BigDecimal balance, List<String> currencyList) {
        // We need to send a request to CydeoDev api to get currency exchange rates
        CurrencyResponse currencyResponse = currencyClient.getCurrenciesByRequest(currencyList);
        var currencyDataList = currencyResponse.getData();
        // Create an empty map to set the values
        Map<String , BigDecimal>specificCurrencies= new LinkedHashMap<>();


        currencyResponse.getData().forEach(eachData->{

                    String currencyCode= eachData.getCurrencyCode();
                    BigDecimal currencyBalance =  eachData.getUsdExchangeRate().multiply(balance).setScale(0,4);
                    specificCurrencies.put(currencyCode,currencyBalance);
                }
        );

        return specificCurrencies;
    }




    private Map<String, BigDecimal> getAllCurrenciesByBalance(BigDecimal balance) {
        // We need to send a request to CydeoDev api to get currency exchange rates
        CurrencyResponse currencyResponse = currencyClient.getAllCurrencies();
        var currencyDataList = currencyResponse.getData();
        // Create an empty map to set the values
        Map<String , BigDecimal>otherCurrencies= new LinkedHashMap<>();


       currencyResponse.getData().forEach(eachData->{

                   String currencyCode= eachData.getCurrencyCode();
                   BigDecimal currencyBalance =  eachData.getUsdExchangeRate().multiply(balance).setScale(0,4);
                   otherCurrencies.put(currencyCode,currencyBalance);
               }
               );

        return otherCurrencies;
    }




    /**
     * Generates a random account number.
     *
     * @return The randomly generated account number.
     */
    private Long generateAccountNumber() {
        return (long) (Math.random() * 1000000000000L);
    }

    /**
     * Creates a new account for the user specified in the accountDTO.
     *
     * @param accountDTO The AccountDTO containing account information.
     * @return The created AccountDTO.
     */
    @Override
    public AccountDTO create(AccountDTO accountDTO) {
        // Find the user by username using the UserService.
        UserDTO user = userService.findByUsername(accountDTO.getUsername());

        // Convert the AccountDTO to an Account entity for database storage.
        Account accountToSave = mapperUtil.convert(accountDTO, new Account());

        // Set the user of the account by converting the UserDTO to a User entity.
        accountToSave.setUser(mapperUtil.convert(user, new User()));

        // Generate a random account number for the new account.
        accountToSave.setAccountNumber(generateAccountNumber());

        // Save the newly created account to the database.
        Account newAccount = accountRepository.save(accountToSave);

        // Convert the saved Account entity back to an AccountDTO for response.
        AccountDTO accountToReturn = mapperUtil.convert(newAccount, new AccountDTO());

        // Set the username in the returned AccountDTO for consistency.
        accountToReturn.setUsername(user.getUsername());

        // Return the created AccountDTO.
        return accountToReturn;
    }



}
