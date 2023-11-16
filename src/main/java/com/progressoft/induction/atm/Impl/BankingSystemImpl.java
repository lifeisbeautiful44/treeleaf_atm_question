package com.progressoft.induction.atm.Impl;

import com.progressoft.induction.atm.BankingSystem;
import com.progressoft.induction.atm.Banknote;
import com.progressoft.induction.atm.exceptions.AccountNotFoundException;
import com.progressoft.induction.atm.exceptions.InsufficientFundsException;
import com.progressoft.induction.atm.exceptions.NotEnoughMoneyInATMException;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class BankingSystemImpl implements BankingSystem {
   Map<String, BigDecimal> accountBalanceMap = new HashMap<String, BigDecimal>();
   EnumMap<Banknote,Integer> atmCashMap = new EnumMap<>(Banknote.class);

    public BankingSystemImpl() {
        atmCashMap.put(Banknote.FIFTY_JOD,10);
        atmCashMap.put(Banknote.TWENTY_JOD,20);
        atmCashMap.put(Banknote.TEN_JOD,100);
        atmCashMap.put(Banknote.FIVE_JOD,100);

        accountBalanceMap.put("123456789", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("111111111", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("222222222", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("333333333", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("444444444", BigDecimal.valueOf(1000.0));
    }

    public BigDecimal sumOfMoneyInAtm(){
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<Banknote, Integer> entry : atmCashMap.entrySet()) {
            BigDecimal banknoteValue = entry.getKey().getValue();
            int banknoteCount = entry.getValue();
            sum = sum.add(banknoteValue.multiply(BigDecimal.valueOf(banknoteCount)));
        }
        return sum;
    }


    @Override
    public BigDecimal getAccountBalance(String accountNumber){
        BigDecimal balance = accountBalanceMap.get(accountNumber);
        if(balance == null)
        {
            throw new AccountNotFoundException("Account not found , " + accountNumber);
        }
        return balance;
    }

    @Override
    public void debitAccount(String accountNumber, BigDecimal amount) {

        BigDecimal accountBalance = accountBalanceMap.get(accountNumber);

        BigDecimal atmBalance = sumOfMoneyInAtm();
        if (atmBalance.compareTo(amount) < 0) {
            throw new NotEnoughMoneyInATMException("Not enough money in ATM to process the withdrawal");
        }

        System.out.println("Subtracting the balance");

        accountBalanceMap.put(accountNumber,accountBalance.subtract(amount));

        BigDecimal remainingAmount = amount;

        for (Map.Entry<Banknote, Integer> entry : atmCashMap.entrySet()) {
            Banknote banknote = entry.getKey();
            int banknoteCount = entry.getValue();

            BigDecimal banknoteValue = banknote.getValue();
            int banknotesToWithdraw = remainingAmount.divideAndRemainder(banknoteValue)[0].min(BigDecimal.valueOf(banknoteCount)).intValue();

            remainingAmount = remainingAmount.subtract(banknoteValue.multiply(BigDecimal.valueOf(banknotesToWithdraw)));
            atmCashMap.put(banknote, banknoteCount - banknotesToWithdraw);

            if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

    }
}
