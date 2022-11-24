package com.josealonso.solid.dip;

public class ShoppingMall {

    public ShoppingMall(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public void doPurchaseItem(long amount) {
        bankCard.doTransaction(amount);
    }

    public static void main(String[] args) {
        // Just by changing this line you can switch between card types
        BankCard bankCard = new CreditCard();
        ShoppingMall shoppingMall = new ShoppingMall(bankCard);
        shoppingMall.doPurchaseItem(200);
    }
}

