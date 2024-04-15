package com.matos.picpay.payments.authorization;

public record Authorization(String message) {
    public boolean isAuthorized(){
        return message.equals("Autorizado");
    }
}
