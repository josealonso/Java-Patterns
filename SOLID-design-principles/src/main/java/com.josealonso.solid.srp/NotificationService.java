package com.josealonso.solid.srp;

public class NotificationService {

    public void sendOTP(String medium) {
        if (medium.equals("email")) {
            // use JavaMailSenderAPI
        }
        if (medium.equals("mobile")) {
            // use twillio API
        }
    }
}
