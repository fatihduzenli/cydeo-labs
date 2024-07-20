package com.cydeo.client;

import com.cydeo.dto.response.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.*;

@FeignClient(url = "http://cydeodev.com/api/v1",name = "CURRENCY-SERVICE")
public interface CurrencyClient {
    @GetMapping("/currency/all")
    CurrencyResponse getAllCurrencies();


    

    //  -> /currency/list?currencies=EUR&currencies=TRY

    @GetMapping("/currency/list")
    CurrencyResponse getCurrenciesByRequest(@RequestParam List<String> currencies);

    public static HashMap<Integer,String > question(){
        return null;
    }
}
