package com.josealonso.solid.srp;

public class BankService {

    public long deposit(long amount, String accountNumber) {
        // deposit amount
        return 0;
    }

    public long withdraw(long amount, String accountNumber) {
        // withdraw amount
        return 0;
    }

    public void printBalance() {
        // This method should be in a different class
    }

    public void sendOTP(String medium) {
        if (medium.equals("email")) {
            // use JavaMailSenderAPI
        }
        if (medium.equals("mobile")) {
            // use twillio API
        }
    }
}